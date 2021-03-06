/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.fabric.netty.handlers;

import com.liferay.portal.fabric.FabricPathMappingVisitor;
import com.liferay.portal.fabric.InputResource;
import com.liferay.portal.fabric.agent.FabricAgent;
import com.liferay.portal.fabric.local.agent.EmbeddedProcessExecutor;
import com.liferay.portal.fabric.local.worker.EmbeddedProcessChannel;
import com.liferay.portal.fabric.local.worker.LocalFabricWorker;
import com.liferay.portal.fabric.netty.NettyTestUtil;
import com.liferay.portal.fabric.netty.agent.NettyFabricAgentStub;
import com.liferay.portal.fabric.netty.fileserver.handlers.FileServerTestUtil;
import com.liferay.portal.fabric.netty.handlers.NettyFabricWorkerExecutionChannelHandler.FabricAgentFinishStartupProcessCallable;
import com.liferay.portal.fabric.netty.handlers.NettyFabricWorkerExecutionChannelHandler.FabricWorkerResultProcessCallable;
import com.liferay.portal.fabric.netty.handlers.NettyFabricWorkerExecutionChannelHandler.LoadedPaths;
import com.liferay.portal.fabric.netty.handlers.NettyFabricWorkerExecutionChannelHandler.PostFabricWorkerExecutionFutureListener;
import com.liferay.portal.fabric.netty.handlers.NettyFabricWorkerExecutionChannelHandler.PostFabricWorkerFinishFutureListener;
import com.liferay.portal.fabric.netty.handlers.NettyFabricWorkerExecutionChannelHandler.PostLoadPathsFutureListener;
import com.liferay.portal.fabric.netty.rpc.ChannelThreadLocal;
import com.liferay.portal.fabric.netty.rpc.RPCSerializable;
import com.liferay.portal.fabric.netty.util.NettyUtilAdvice;
import com.liferay.portal.fabric.netty.worker.NettyFabricWorkerConfig;
import com.liferay.portal.fabric.netty.worker.NettyFabricWorkerStub;
import com.liferay.portal.fabric.repository.MockRepository;
import com.liferay.portal.fabric.worker.FabricWorker;
import com.liferay.portal.kernel.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.concurrent.FutureListener;
import com.liferay.portal.kernel.concurrent.NoticeableFuture;
import com.liferay.portal.kernel.process.ProcessCallable;
import com.liferay.portal.kernel.process.ProcessConfig;
import com.liferay.portal.kernel.process.ProcessConfig.Builder;
import com.liferay.portal.kernel.process.ProcessException;
import com.liferay.portal.kernel.process.local.ReturnProcessCallable;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ObjectGraphUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.test.AdviseWith;
import com.liferay.portal.test.runners.AspectJMockingNewClassLoaderJUnitTestRunner;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.concurrent.DefaultPromise;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(AspectJMockingNewClassLoaderJUnitTestRunner.class)
public class NettyFabricWorkerExecutionChannelHandlerTest {

	@ClassRule
	public static CodeCoverageAssertor codeCoverageAssertor =
		new CodeCoverageAssertor();

	@Before
	public void setUp() {
		ChannelThreadLocal.setChannel(_embeddedChannel);
	}

	@After
	public void tearDown() {
		FileServerTestUtil.cleanUp();

		ChannelThreadLocal.removeChannel();
	}

	@Test
	public void testConstructor() {
		try {
			new NettyFabricWorkerExecutionChannelHandler(null, null, 0);

			Assert.fail();
		}
		catch (NullPointerException npe) {
			Assert.assertEquals("Repository is null", npe.getMessage());
		}

		try {
			new NettyFabricWorkerExecutionChannelHandler(
				new MockRepository(), null, 0);

			Assert.fail();
		}
		catch (NullPointerException npe) {
			Assert.assertEquals("Process executor is null", npe.getMessage());
		}

		new NettyFabricWorkerExecutionChannelHandler(
			new MockRepository(), new EmbeddedProcessExecutor(), 0);
	}

	@Test
	public void testExceptionCaught() {
		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository(), new EmbeddedProcessExecutor(),
					Long.MAX_VALUE);

		ChannelPipeline channelPipeline = _embeddedChannel.pipeline();

		channelPipeline.addFirst(nettyFabricWorkerExecutionChannelHandler);

		NettyFabricWorkerConfig<Serializable> nettyFabricWorkerConfig =
			createNettyFabricWorkerConfig();

		ReflectionTestUtil.setFieldValue(
			nettyFabricWorkerConfig, "_processConfig", null);

		CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(
			NettyFabricWorkerExecutionChannelHandler.class.getName(),
			Level.INFO);

		try {
			String embeddedChannelToString = _embeddedChannel.toString();

			_embeddedChannel.writeInbound(nettyFabricWorkerConfig);

			Assert.assertFalse(_embeddedChannel.isOpen());

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(2, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Closing " + embeddedChannelToString + " due to:",
				logRecord.getMessage());

			Throwable throwable = logRecord.getThrown();

			Assert.assertSame(NullPointerException.class, throwable.getClass());

			logRecord = logRecords.get(1);

			Assert.assertEquals(
				_embeddedChannel + " is closed", logRecord.getMessage());
		}
		finally {
			captureHandler.close();
		}
	}

	@Test
	public void testFabricAgentFinishStartupProcessCallable()
		throws ProcessException {

		FabricAgentFinishStartupProcessCallable
			fabricWorkerFinishStartupProcessCallable =
				new FabricAgentFinishStartupProcessCallable(0);

		try {
			fabricWorkerFinishStartupProcessCallable.call();

			Assert.fail();
		}
		catch (ProcessException pe) {
			Assert.assertEquals(
				"Unable to locate fabric agent on channel " + _embeddedChannel,
				pe.getMessage());
		}

		NettyFabricAgentStub nettyFabricAgentStub =
			installNettyFabricAgentStub();

		Map<Long, DefaultNoticeableFuture<?>> startupNoticeableFutures =
			ReflectionTestUtil.getFieldValue(
				nettyFabricAgentStub, "_startupNoticeableFutures");

		DefaultNoticeableFuture<?> defaultNoticeableFuture =
			new DefaultNoticeableFuture<Object>();

		startupNoticeableFutures.put(0L, defaultNoticeableFuture);

		Assert.assertFalse(defaultNoticeableFuture.isDone());
		Assert.assertNull(fabricWorkerFinishStartupProcessCallable.call());
		Assert.assertTrue(defaultNoticeableFuture.isDone());
	}

	@Test
	public void testFabricWorkerResultProcessCallable() throws Exception {

		// Unable to locate fabric agent

		FabricWorkerResultProcessCallable fabricWorkerResultProcessCallable =
			new FabricWorkerResultProcessCallable(0, StringPool.BLANK, null);

		try {
			fabricWorkerResultProcessCallable.call();

			Assert.fail();
		}
		catch (ProcessException pe) {
			Assert.assertEquals(
				"Unable to locate fabric agent on channel " + _embeddedChannel,
				pe.getMessage());
		}

		// Unable to locate fabric worker

		installNettyFabricAgentStub();

		try {
			fabricWorkerResultProcessCallable.call();

			Assert.fail();
		}
		catch (ProcessException pe) {
			Assert.assertEquals(
				"Unable to locate fabric worker on channel " +
					_embeddedChannel + ", with fabric worker id 0",
				pe.getMessage());
		}

		// Finish with result

		NettyFabricWorkerStub<Serializable> nettyFabricWorkerStub =
			installNettyFabricWorkerStub();

		NoticeableFuture<Serializable> noticeableFuture =
			nettyFabricWorkerStub.getProcessNoticeableFuture();

		Assert.assertNull(fabricWorkerResultProcessCallable.call());
		Assert.assertEquals(StringPool.BLANK, noticeableFuture.get());

		// Finish with exception

		Throwable throwable = new Throwable();

		fabricWorkerResultProcessCallable =
			new FabricWorkerResultProcessCallable(0, null, throwable);

		nettyFabricWorkerStub = installNettyFabricWorkerStub();

		noticeableFuture = nettyFabricWorkerStub.getProcessNoticeableFuture();

		Assert.assertNull(fabricWorkerResultProcessCallable.call());

		try {
			noticeableFuture.get();

			Assert.fail();
		}
		catch (ExecutionException ee) {
			Assert.assertSame(throwable, ee.getCause());
		}
	}

	@AdviseWith(adviceClasses = NettyUtilAdvice.class)
	@Test
	public void testIntegration() {
		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository(), new EmbeddedProcessExecutor(),
					Long.MAX_VALUE);

		ChannelPipeline channelPipeline = _embeddedChannel.pipeline();

		channelPipeline.addFirst(nettyFabricWorkerExecutionChannelHandler);

		_embeddedChannel.writeInbound(createNettyFabricWorkerConfig());

		FabricWorker<Serializable> fabricWorker =
			NettyChannelAttributes.getFabricWorker(_embeddedChannel, 0);

		DefaultNoticeableFuture<?> defaultNoticeableFuture =
			(DefaultNoticeableFuture<?>)
				fabricWorker.getProcessNoticeableFuture();

		defaultNoticeableFuture.run();

		Assert.assertNull(
			NettyChannelAttributes.getFabricWorker(_embeddedChannel, 0));
	}

	@Test
	public void testLoadedPaths() {
		Map<Path, Path> inputPaths = Collections.emptyMap();

		String newBootstrapClassPath = "newBootstrapClassPath";
		String newRuntimeClassPath = "newRuntimeClassPath";

		LoadedPaths loadedPaths = new LoadedPaths(
			inputPaths, newBootstrapClassPath, newRuntimeClassPath);

		Assert.assertSame(inputPaths, loadedPaths.getInputPaths());

		List<String> arguments = Collections.emptyList();

		Builder builder = new Builder();

		builder.setArguments(arguments);
		builder.setBootstrapClassPath("oldBootstrapClassPath");
		builder.setRuntimeClassPath("oldRuntimeClassPath");

		ProcessConfig processConfig = loadedPaths.toProcessConfig(
			builder.build());

		Assert.assertSame(arguments, processConfig.getArguments());
		Assert.assertEquals(
			newBootstrapClassPath, processConfig.getBootstrapClassPath());
		Assert.assertEquals(
			newRuntimeClassPath, processConfig.getRuntimeClassPath());
	}

	@Test
	public void testLoadPaths() throws Exception {
		final Map<Path, Path> mergedPaths = new HashMap<Path, Path>();

		Path inputPath1 = getAbsolutePath("inputPaths1");
		Path mappedInputPath1 = getAbsolutePath("mappedInputPath1");
		Path inputPath2 = getAbsolutePath("inputPaths2");
		Path mappedInputPath2 = getAbsolutePath("mappedInputPath2");

		mergedPaths.put(inputPath1, mappedInputPath1);
		mergedPaths.put(inputPath2, mappedInputPath2);

		Path bootstrapPath1 = getAbsolutePath("bootstrapPath1");
		Path mappedBootstrapPath1 = getAbsolutePath("mappedBootstrapPath1");
		Path bootstrapPath2 = getAbsolutePath("bootstrapPath2");
		Path mappedBootstrapPath2 = getAbsolutePath("mappedBootstrapPath2");
		Path bootstrapPath3 = getAbsolutePath("bootstrapPath3");
		Path mappedBootstrapPath3 = getAbsolutePath("mappedBootstrapPath3");

		mergedPaths.put(bootstrapPath1, mappedBootstrapPath1);
		mergedPaths.put(bootstrapPath2, mappedBootstrapPath2);
		mergedPaths.put(bootstrapPath3, mappedBootstrapPath3);

		Path runtimePath1 = getAbsolutePath("runtimePath1");
		Path mappedRuntimePath1 = getAbsolutePath("mappedRuntimePath1");
		Path runtimePath2 = getAbsolutePath("runtimePath2");
		Path mappedRuntimePath2 = getAbsolutePath("mappedRuntimePath2");
		Path runtimePath3 = getAbsolutePath("runtimePath3");
		Path mappedRuntimePath3 = getAbsolutePath("mappedRuntimePath3");

		mergedPaths.put(runtimePath1, mappedRuntimePath1);
		mergedPaths.put(runtimePath2, mappedRuntimePath2);
		mergedPaths.put(runtimePath3, mappedRuntimePath3);

		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository() {

						@Override
						public NoticeableFuture<Map<Path, Path>> getFiles(
							Map<Path, Path> pathMap, boolean deleteAfterFetch) {

							DefaultNoticeableFuture<Map<Path, Path>>
								defaultNoticeableFuture =
									new DefaultNoticeableFuture
										<Map<Path, Path>>();

							defaultNoticeableFuture.set(mergedPaths);

							return defaultNoticeableFuture;
						}

						@Override
						public Path getRepositoryPath() {
							return getAbsolutePath("repository");
						}

					},
					new EmbeddedProcessExecutor(), 0);

		Builder builder = new Builder();

		builder.setBootstrapClassPath(
			bootstrapPath1 + File.pathSeparator + bootstrapPath2 +
				File.pathSeparator + bootstrapPath3);
		builder.setRuntimeClassPath(
			runtimePath1 + File.pathSeparator + runtimePath2 +
				File.pathSeparator + runtimePath3);

		ProcessConfig processConfig = builder.build();

		ProcessCallable<Serializable> processCallable =
			new LoadPathProcessCallable(
				inputPath1.toFile(), inputPath2.toFile());

		FabricPathMappingVisitor fabricPathMappingVisitor =
			new FabricPathMappingVisitor(
				InputResource.class, getAbsolutePath("repository"));

		ObjectGraphUtil.walkObjectGraph(
			processCallable, fabricPathMappingVisitor);

		NoticeableFuture<LoadedPaths> noticeableFuture =
			nettyFabricWorkerExecutionChannelHandler.loadPaths(
				new NettyFabricWorkerConfig<Serializable>(
					0, processConfig, processCallable,
					fabricPathMappingVisitor.getPathMap()));

		LoadedPaths loadedPaths = noticeableFuture.get();

		Map<Path, Path> loadedInputPaths = loadedPaths.getInputPaths();

		Assert.assertEquals(2, loadedInputPaths.size());
		Assert.assertEquals(mappedInputPath1, loadedInputPaths.get(inputPath1));
		Assert.assertEquals(mappedInputPath2, loadedInputPaths.get(inputPath2));

		processConfig = loadedPaths.toProcessConfig(processConfig);

		Assert.assertEquals(
			mappedBootstrapPath1 + File.pathSeparator +
				mappedBootstrapPath2 + File.pathSeparator +
					mappedBootstrapPath3,
			processConfig.getBootstrapClassPath());
		Assert.assertEquals(
			mappedRuntimePath1 + File.pathSeparator +
				mappedRuntimePath2 + File.pathSeparator +
					mappedRuntimePath3,
			processConfig.getRuntimeClassPath());
	}

	@Test
	public void testLoadPathsMissedBootstrapPaths() throws Exception {

		// With log

		final Map<Path, Path> mergedPaths = new HashMap<Path, Path>();

		Path bootstrapPath1 = getAbsolutePath("bootstrapPath1");
		Path mappedBootstrapPath1 = getAbsolutePath("mappedBootstrapPath1");
		Path bootstrapPath2 = getAbsolutePath("bootstrapPath2");
		Path bootstrapPath3 = getAbsolutePath("bootstrapPath3");

		mergedPaths.put(bootstrapPath1, mappedBootstrapPath1);

		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository() {

						@Override
						public NoticeableFuture<Map<Path, Path>> getFiles(
							Map<Path, Path> pathMap, boolean deleteAfterFetch) {

							DefaultNoticeableFuture<Map<Path, Path>>
								defaultNoticeableFuture =
									new DefaultNoticeableFuture
										<Map<Path, Path>>();

							defaultNoticeableFuture.set(mergedPaths);

							return defaultNoticeableFuture;
						}

						@Override
						public Path getRepositoryPath() {
							return getAbsolutePath("repository");
						}

					},
					new EmbeddedProcessExecutor(), 0);

		Builder builder = new Builder();

		builder.setBootstrapClassPath(
			bootstrapPath1 + File.pathSeparator + bootstrapPath2 +
				File.pathSeparator + bootstrapPath3);
		builder.setRuntimeClassPath(StringPool.BLANK);

		ProcessConfig processConfig = builder.build();

		ProcessCallable<Serializable> processCallable =
			new ReturnProcessCallable<Serializable>(null);

		FabricPathMappingVisitor fabricPathMappingVisitor =
			new FabricPathMappingVisitor(
				InputResource.class, getAbsolutePath("repository"));

		ObjectGraphUtil.walkObjectGraph(
			processCallable, fabricPathMappingVisitor);

		CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(
			NettyFabricWorkerExecutionChannelHandler.class.getName(),
			Level.WARNING);

		try {
			NoticeableFuture<LoadedPaths> noticeableFuture =
				nettyFabricWorkerExecutionChannelHandler.loadPaths(
					new NettyFabricWorkerConfig<Serializable>(
						0, processConfig, processCallable,
						fabricPathMappingVisitor.getPathMap()));

			LoadedPaths loadedPaths = noticeableFuture.get();

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Incomplete bootstrap classpath loaded, missed: " +
					Arrays.asList(bootstrapPath2, bootstrapPath3),
				logRecord.getMessage());

			Map<Path, Path> loadedInputPaths = loadedPaths.getInputPaths();

			Assert.assertTrue(loadedInputPaths.isEmpty());

			ProcessConfig loadedProcessConfig = loadedPaths.toProcessConfig(
				processConfig);

			Assert.assertEquals(
				mappedBootstrapPath1.toString(),
				loadedProcessConfig.getBootstrapClassPath());
			Assert.assertEquals(
				StringPool.BLANK, loadedProcessConfig.getRuntimeClassPath());
		}
		finally {
			captureHandler.close();
		}

		// Without log

		captureHandler = JDKLoggerTestUtil.configureJDKLogger(
			NettyFabricWorkerExecutionChannelHandler.class.getName(),
			Level.OFF);

		try {
			NoticeableFuture<LoadedPaths> noticeableFuture =
				nettyFabricWorkerExecutionChannelHandler.loadPaths(
					new NettyFabricWorkerConfig<Serializable>(
						0, processConfig, processCallable,
						fabricPathMappingVisitor.getPathMap()));

			LoadedPaths loadedPaths = noticeableFuture.get();

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertTrue(logRecords.isEmpty());

			Map<Path, Path> loadedInputPaths = loadedPaths.getInputPaths();

			Assert.assertTrue(loadedInputPaths.isEmpty());

			ProcessConfig loadedProcessConfig = loadedPaths.toProcessConfig(
				processConfig);

			Assert.assertEquals(
				mappedBootstrapPath1.toString(),
				loadedProcessConfig.getBootstrapClassPath());
			Assert.assertEquals(
				StringPool.BLANK, loadedProcessConfig.getRuntimeClassPath());
		}
		finally {
			captureHandler.close();
		}
	}

	@Test
	public void testLoadPathsMissedInputPaths() throws InterruptedException {
		final Map<Path, Path> mergedPaths = new HashMap<Path, Path>();

		Path inputPath1 = getAbsolutePath("inputPaths1");
		Path mappedInputPath1 = getAbsolutePath("mappedInputPath1");
		Path inputPath2 = getAbsolutePath("inputPaths2");

		mergedPaths.put(inputPath1, mappedInputPath1);

		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository() {

						@Override
						public NoticeableFuture<Map<Path, Path>> getFiles(
							Map<Path, Path> pathMap, boolean deleteAfterFetch) {

							DefaultNoticeableFuture<Map<Path, Path>>
								defaultNoticeableFuture =
									new DefaultNoticeableFuture
										<Map<Path, Path>>();

							defaultNoticeableFuture.set(mergedPaths);

							return defaultNoticeableFuture;
						}

						@Override
						public Path getRepositoryPath() {
							return getAbsolutePath("repository");
						}

					},
					new EmbeddedProcessExecutor(), 0);

		Builder builder = new Builder();

		builder.setBootstrapClassPath(StringPool.BLANK);
		builder.setRuntimeClassPath(StringPool.BLANK);

		ProcessConfig processConfig = builder.build();

		ProcessCallable<Serializable> processCallable =
			new LoadPathProcessCallable(
				inputPath1.toFile(), inputPath2.toFile());

		FabricPathMappingVisitor fabricPathMappingVisitor =
			new FabricPathMappingVisitor(
				InputResource.class, getAbsolutePath("repository"));

		ObjectGraphUtil.walkObjectGraph(
			processCallable, fabricPathMappingVisitor);

		NoticeableFuture<LoadedPaths> noticeableFuture =
			nettyFabricWorkerExecutionChannelHandler.loadPaths(
				new NettyFabricWorkerConfig<Serializable>(
					0, processConfig, processCallable,
					fabricPathMappingVisitor.getPathMap()));

		try {
			noticeableFuture.get();
		}
		catch (ExecutionException ee) {
			Throwable throwable = ee.getCause();

			Assert.assertSame(IOException.class, throwable.getClass());
			Assert.assertEquals(
				"Unable to get input paths: " +
					Arrays.asList(inputPath2),
				throwable.getMessage());
		}
	}

	@Test
	public void testLoadPathsMissedRuntimePaths() throws Exception {

		// With log

		final Map<Path, Path> mergedPaths = new HashMap<Path, Path>();

		Path runtimePath1 = getAbsolutePath("runtimePath1");
		Path mappedRuntimePath1 = getAbsolutePath("mappedRuntimePath1");
		Path runtimePath2 = getAbsolutePath("runtimePath2");
		Path runtimePath3 = getAbsolutePath("runtimePath3");
		Path mappedRuntimePath3 = getAbsolutePath("mappedRuntimePath3");

		mergedPaths.put(runtimePath1, mappedRuntimePath1);
		mergedPaths.put(runtimePath3, mappedRuntimePath3);

		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository() {

						@Override
						public NoticeableFuture<Map<Path, Path>> getFiles(
							Map<Path, Path> pathMap, boolean deleteAfterFetch) {

							DefaultNoticeableFuture<Map<Path, Path>>
								defaultNoticeableFuture =
									new DefaultNoticeableFuture
										<Map<Path, Path>>();

							defaultNoticeableFuture.set(mergedPaths);

							return defaultNoticeableFuture;
						}

						@Override
						public Path getRepositoryPath() {
							return getAbsolutePath("repository");
						}

					},
					new EmbeddedProcessExecutor(), 0);

		Builder builder = new Builder();

		builder.setBootstrapClassPath(StringPool.BLANK);
		builder.setRuntimeClassPath(
			runtimePath1 + File.pathSeparator + runtimePath2 +
				File.pathSeparator + runtimePath3);

		ProcessConfig processConfig = builder.build();

		ProcessCallable<Serializable> processCallable =
			new ReturnProcessCallable<Serializable>(null);

		FabricPathMappingVisitor fabricPathMappingVisitor =
			new FabricPathMappingVisitor(
				InputResource.class, getAbsolutePath("repository"));

		ObjectGraphUtil.walkObjectGraph(
			processCallable, fabricPathMappingVisitor);

		CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(
			NettyFabricWorkerExecutionChannelHandler.class.getName(),
			Level.WARNING);

		try {
			NoticeableFuture<LoadedPaths> noticeableFuture =
				nettyFabricWorkerExecutionChannelHandler.loadPaths(
					new NettyFabricWorkerConfig<Serializable>(
						0, processConfig, processCallable,
						fabricPathMappingVisitor.getPathMap()));

			LoadedPaths loadedPaths = noticeableFuture.get();

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Incomplete runtime classpath loaded, missed: " +
					Arrays.asList(runtimePath2),
				logRecord.getMessage());

			Map<Path, Path> loadedInputPaths = loadedPaths.getInputPaths();

			Assert.assertTrue(loadedInputPaths.isEmpty());

			ProcessConfig loadedProcessConfig = loadedPaths.toProcessConfig(
				processConfig);

			Assert.assertEquals(
				StringPool.BLANK, loadedProcessConfig.getBootstrapClassPath());
			Assert.assertEquals(
				mappedRuntimePath1 + File.pathSeparator +
					mappedRuntimePath3,
				loadedProcessConfig.getRuntimeClassPath());
		}
		finally {
			captureHandler.close();
		}

		// Without log

		captureHandler = JDKLoggerTestUtil.configureJDKLogger(
			NettyFabricWorkerExecutionChannelHandler.class.getName(),
			Level.OFF);

		try {
			NoticeableFuture<LoadedPaths> noticeableFuture =
				nettyFabricWorkerExecutionChannelHandler.loadPaths(
					new NettyFabricWorkerConfig<Serializable>(
						0, processConfig, processCallable,
						fabricPathMappingVisitor.getPathMap()));

			LoadedPaths loadedPaths = noticeableFuture.get();

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertTrue(logRecords.isEmpty());

			Map<Path, Path> loadedInputPaths = loadedPaths.getInputPaths();

			Assert.assertTrue(loadedInputPaths.isEmpty());

			ProcessConfig loadedProcessConfig = loadedPaths.toProcessConfig(
				processConfig);

			Assert.assertEquals(
				StringPool.BLANK, loadedProcessConfig.getBootstrapClassPath());
			Assert.assertEquals(
				mappedRuntimePath1 + File.pathSeparator +
					mappedRuntimePath3,
				loadedProcessConfig.getRuntimeClassPath());
		}
		finally {
			captureHandler.close();
		}
	}

	@AdviseWith(adviceClasses = NettyUtilAdvice.class)
	@Test
	public void testPostFabricWorkerExecutionFutureListener() throws Exception {

		// Execution failure

		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository(), new EmbeddedProcessExecutor(),
					Long.MAX_VALUE);

		PostFabricWorkerExecutionFutureListener
			postFabricWorkerExecutionFutureListener =
				nettyFabricWorkerExecutionChannelHandler.
					new PostFabricWorkerExecutionFutureListener(
						_embeddedChannel, null,
						createNettyFabricWorkerConfig());

		DefaultPromise<FabricWorker<Serializable>> defaultPromise =
			new DefaultPromise<FabricWorker<Serializable>>(
				_embeddedChannel.eventLoop());

		Throwable throwable = new Throwable();

		defaultPromise.setFailure(throwable);

		installNettyFabricAgentStub();

		NettyFabricWorkerStub<Serializable> nettyFabricWorkerStub =
			installNettyFabricWorkerStub();

		NoticeableFuture<Serializable> noticeableFuture =
			nettyFabricWorkerStub.getProcessNoticeableFuture();

		defaultPromise.addListener(postFabricWorkerExecutionFutureListener);

		invokeRPC();

		try {
			noticeableFuture.get();

			Assert.fail();
		}
		catch (ExecutionException ee) {
			Assert.assertEquals(throwable, ee.getCause());
		}

		// Finish startup failure

		_embeddedChannel.close();

		postFabricWorkerExecutionFutureListener =
			nettyFabricWorkerExecutionChannelHandler.
				new PostFabricWorkerExecutionFutureListener(
					_embeddedChannel, null, createNettyFabricWorkerConfig());

		defaultPromise = new DefaultPromise<FabricWorker<Serializable>>(
			_embeddedChannel.eventLoop());

		DefaultNoticeableFuture<Serializable> processNoticeableFuture =
			new DefaultNoticeableFuture<Serializable>();

		FabricWorker<Serializable> fabricWorker =
			new LocalFabricWorker<Serializable>(
				new EmbeddedProcessChannel<Serializable>(
					processNoticeableFuture));

		defaultPromise.setSuccess(fabricWorker);

		nettyFabricWorkerStub = installNettyFabricWorkerStub();

		noticeableFuture = nettyFabricWorkerStub.getProcessNoticeableFuture();

		CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(
			NettyFabricWorkerExecutionChannelHandler.class.getName(),
			Level.SEVERE);

		try {
			defaultPromise.addListener(postFabricWorkerExecutionFutureListener);

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Unable to finish fabric worker startup",
				logRecord.getMessage());
		}
		finally {
			captureHandler.close();
		}

		Assert.assertSame(
			fabricWorker,
			NettyChannelAttributes.getFabricWorker(_embeddedChannel, 0));

		Set<FutureListener<Serializable>> futureListeners =
			ReflectionTestUtil.getFieldValue(
				processNoticeableFuture, "_futureListeners");

		Assert.assertEquals(2, futureListeners.size());

		Iterator<FutureListener<Serializable>> iterator =
			futureListeners.iterator();

		iterator.next();

		FutureListener<Serializable> futureListener = iterator.next();

		futureListener = ReflectionTestUtil.getFieldValue(
			futureListener, "_futureListener");

		Assert.assertSame(
			PostFabricWorkerFinishFutureListener.class,
			futureListener.getClass());
	}

	@AdviseWith(adviceClasses = NettyUtilAdvice.class)
	@Test
	public void testPostFabricWorkerFinishFutureListener() throws Exception {

		// Finish with execution exception

		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository(), new EmbeddedProcessExecutor(),
					Long.MAX_VALUE);

		Path inputPath1 = FileServerTestUtil.createEmptyFile(
			Paths.get("inputPath1"));
		Path inputPath2 = FileServerTestUtil.createEmptyFile(
			Paths.get("inputPath2"));

		Map<Path, Path> inputPaths = new HashMap<Path, Path>();

		inputPaths.put(inputPath1, inputPath1);
		inputPaths.put(inputPath2, inputPath2);

		PostFabricWorkerFinishFutureListener
			postFabricWorkerFinishFutureListener =
				nettyFabricWorkerExecutionChannelHandler.
					new PostFabricWorkerFinishFutureListener(
						_embeddedChannel, createNettyFabricWorkerConfig(),
						new LoadedPaths(inputPaths, null, null));

		DefaultNoticeableFuture<Serializable> defaultNoticeableFuture =
			new DefaultNoticeableFuture<Serializable>();

		Throwable throwable = new Throwable();

		defaultNoticeableFuture.setException(throwable);

		installNettyFabricAgentStub();

		NettyFabricWorkerStub<Serializable> nettyFabricWorkerStub =
			installNettyFabricWorkerStub();

		NoticeableFuture<?> noticeableFuture =
			nettyFabricWorkerStub.getProcessNoticeableFuture();

		defaultNoticeableFuture.addFutureListener(
			postFabricWorkerFinishFutureListener);

		Assert.assertTrue(Files.notExists(inputPath1));
		Assert.assertTrue(Files.notExists(inputPath2));

		invokeRPC();

		try {
			noticeableFuture.get();

			Assert.fail();
		}
		catch (ExecutionException ee) {
			Assert.assertEquals(throwable, ee.getCause());
		}

		// Finish with null pointer exception

		nettyFabricWorkerStub = installNettyFabricWorkerStub();

		noticeableFuture = nettyFabricWorkerStub.getProcessNoticeableFuture();

		postFabricWorkerFinishFutureListener.complete(null);

		invokeRPC();

		try {
			noticeableFuture.get();

			Assert.fail();
		}
		catch (ExecutionException ee) {
			Throwable t = ee.getCause();

			Assert.assertSame(NullPointerException.class, t.getClass());
		}

		// Finish with result

		inputPath1 = FileServerTestUtil.createEmptyFile(
			Paths.get("inputPath1"));
		inputPath2 = FileServerTestUtil.createEmptyFile(
			Paths.get("inputPath2"));

		inputPaths = new HashMap<Path, Path>();

		inputPaths.put(inputPath1, inputPath1);
		inputPaths.put(inputPath2, inputPath2);

		postFabricWorkerFinishFutureListener =
			nettyFabricWorkerExecutionChannelHandler.
				new PostFabricWorkerFinishFutureListener(
					_embeddedChannel, createNettyFabricWorkerConfig(),
					new LoadedPaths(inputPaths, null, null));

		defaultNoticeableFuture = new DefaultNoticeableFuture<Serializable>();

		defaultNoticeableFuture.set(StringPool.BLANK);

		nettyFabricWorkerStub = installNettyFabricWorkerStub();

		noticeableFuture = nettyFabricWorkerStub.getProcessNoticeableFuture();

		defaultNoticeableFuture.addFutureListener(
			postFabricWorkerFinishFutureListener);

		Assert.assertTrue(Files.notExists(inputPath1));
		Assert.assertTrue(Files.notExists(inputPath2));

		invokeRPC();

		Assert.assertEquals(StringPool.BLANK, noticeableFuture.get());
	}

	@AdviseWith(adviceClasses = NettyUtilAdvice.class)
	@Test
	public void testPostLoadPathsFutureListener() throws Exception {

		// Load paths failure

		ChannelPipeline channelPipeline = _embeddedChannel.pipeline();

		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository(), new EmbeddedProcessExecutor(),
					Long.MAX_VALUE);

		channelPipeline.addLast(nettyFabricWorkerExecutionChannelHandler);

		ChannelHandlerContext channelHandlerContext =
			channelPipeline.lastContext();

		PostLoadPathsFutureListener postLoadPathsFutureListener =
			nettyFabricWorkerExecutionChannelHandler.
				new PostLoadPathsFutureListener(
					channelHandlerContext, createNettyFabricWorkerConfig());

		DefaultNoticeableFuture<LoadedPaths> defaultNoticeableFuture =
			new DefaultNoticeableFuture<LoadedPaths>();

		Throwable throwable = new Throwable();

		defaultNoticeableFuture.setException(throwable);

		installNettyFabricAgentStub();

		NettyFabricWorkerStub<Serializable> nettyFabricWorkerStub =
			installNettyFabricWorkerStub();

		NoticeableFuture<Serializable> noticeableFuture =
			nettyFabricWorkerStub.getProcessNoticeableFuture();

		defaultNoticeableFuture.addFutureListener(postLoadPathsFutureListener);

		invokeRPC();

		try {
			noticeableFuture.get();

			Assert.fail();
		}
		catch (ExecutionException ee) {
			Assert.assertSame(throwable, ee.getCause());
		}

		// Loaded paths

		defaultNoticeableFuture = new DefaultNoticeableFuture<LoadedPaths>();

		defaultNoticeableFuture.set(
			new LoadedPaths(Collections.<Path, Path>emptyMap(), null, null));

		nettyFabricWorkerStub = installNettyFabricWorkerStub();

		noticeableFuture = nettyFabricWorkerStub.getProcessNoticeableFuture();

		defaultNoticeableFuture.addFutureListener(postLoadPathsFutureListener);

		_embeddedChannel.runPendingTasks();

		FabricAgent fabricAgent = ReflectionTestUtil.getFieldValue(
			nettyFabricWorkerExecutionChannelHandler, "_fabricAgent");

		Collection<? extends FabricWorker<?>> fabricWorkers =
			fabricAgent.getFabricWorkers();

		Assert.assertEquals(1, fabricWorkers.size());
		Assert.assertFalse(noticeableFuture.isDone());
	}

	@AdviseWith(adviceClasses = NettyUtilAdvice.class)
	@Test
	public void testSendResult() throws Exception {

		// Unable to send back fabric worker result

		NettyFabricWorkerExecutionChannelHandler
			nettyFabricWorkerExecutionChannelHandler =
				new NettyFabricWorkerExecutionChannelHandler(
					new MockRepository(), new EmbeddedProcessExecutor(),
					Long.MAX_VALUE);

		Channel channel = NettyTestUtil.createEmptyEmbeddedChannel();

		channel.close();

		CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(
			NettyFabricWorkerExecutionChannelHandler.class.getName(),
			Level.SEVERE);

		try {
			nettyFabricWorkerExecutionChannelHandler.sendResult(
				channel, 0, StringPool.BLANK, null);

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Unable to send back fabric worker result " +
					"{id=0, result=, throwable=null}",
				logRecord.getMessage());
		}
		finally {
			captureHandler.close();
		}

		// Send back result

		installNettyFabricAgentStub();

		NettyFabricWorkerStub<Serializable> nettyFabricWorkerStub =
			installNettyFabricWorkerStub();

		NoticeableFuture<Serializable> noticeableFuture =
			nettyFabricWorkerStub.getProcessNoticeableFuture();

		nettyFabricWorkerExecutionChannelHandler.sendResult(
			_embeddedChannel, 0, StringPool.BLANK, null);

		invokeRPC();

		Assert.assertEquals(StringPool.BLANK, noticeableFuture.get());

		// Send back exception

		nettyFabricWorkerStub = installNettyFabricWorkerStub();

		noticeableFuture = nettyFabricWorkerStub.getProcessNoticeableFuture();

		Throwable throwable = new Throwable();

		nettyFabricWorkerExecutionChannelHandler.sendResult(
			_embeddedChannel, 0, null, throwable);

		invokeRPC();

		try {
			noticeableFuture.get();

			Assert.fail();
		}
		catch (ExecutionException ee) {
			Assert.assertSame(throwable, ee.getCause());
		}
	}

	protected static Path getAbsolutePath(String pathString) {
		Path path = Paths.get(pathString);

		return path.toAbsolutePath();
	}

	protected NettyFabricWorkerConfig<Serializable>
		createNettyFabricWorkerConfig() {

		Builder builder = new Builder();

		builder.setBootstrapClassPath(StringPool.BLANK);
		builder.setRuntimeClassPath(StringPool.BLANK);

		return new NettyFabricWorkerConfig<Serializable>(
			0, builder.build(), new ReturnProcessCallable<Serializable>(null),
			Collections.<Path, Path>emptyMap());
	}

	protected NettyFabricAgentStub installNettyFabricAgentStub() {
		NettyFabricAgentStub nettyFabricAgentStub = new NettyFabricAgentStub(
			_embeddedChannel, new MockRepository(),
			Paths.get("remoteRepositoryPath"), 0, Long.MAX_VALUE);

		NettyChannelAttributes.setNettyFabricAgentStub(
			_embeddedChannel, nettyFabricAgentStub);

		return nettyFabricAgentStub;
	}

	protected NettyFabricWorkerStub<Serializable>
		installNettyFabricWorkerStub() {

		NettyFabricAgentStub nettyFabricAgentStub =
			NettyChannelAttributes.getNettyFabricAgentStub(_embeddedChannel);

		Map<Long, NettyFabricWorkerStub<?>> nettyFabricWorkerStubs =
			ReflectionTestUtil.getFieldValue(
				nettyFabricAgentStub, "_nettyFabricWorkerStubs");

		NettyFabricWorkerStub<Serializable> nettyFabricWorkerStub =
			new NettyFabricWorkerStub<Serializable>(
				0, _embeddedChannel, new MockRepository(),
				Collections.<Path, Path>emptyMap(), 0);

		nettyFabricWorkerStubs.put(0L, nettyFabricWorkerStub);

		return nettyFabricWorkerStub;
	}

	protected void invokeRPC() {
		RPCSerializable rpcSerializable =
			(RPCSerializable)_embeddedChannel.readOutbound();

		rpcSerializable.execute(_embeddedChannel);

		Queue<Object> messages = _embeddedChannel.outboundMessages();

		messages.clear();
	}

	private final EmbeddedChannel _embeddedChannel =
		NettyTestUtil.createEmptyEmbeddedChannel();

	private static class LoadPathProcessCallable
		implements ProcessCallable<Serializable> {

		public LoadPathProcessCallable(File inputFile1, File inputFile2) {
			_inputFile1 = inputFile1;
			_inputFile2 = inputFile2;
		}

		@Override
		public Serializable call() {
			return null;
		}

		private static final long serialVersionUID = 1L;

		@InputResource
		private final File _inputFile1;

		@InputResource
		private final File _inputFile2;

	}

}