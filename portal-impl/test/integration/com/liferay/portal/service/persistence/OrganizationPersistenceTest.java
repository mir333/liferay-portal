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

package com.liferay.portal.service.persistence;

import com.liferay.portal.NoSuchOrganizationException;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.impl.OrganizationModelImpl;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.test.TransactionalTestRule;
import com.liferay.portal.test.runners.PersistenceIntegrationJUnitTestRunner;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.test.RandomTestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @generated
 */
@RunWith(PersistenceIntegrationJUnitTestRunner.class)
public class OrganizationPersistenceTest {
	@ClassRule
	public static TransactionalTestRule transactionalTestRule = new TransactionalTestRule(Propagation.REQUIRED);

	@BeforeClass
	public static void setupClass() throws TemplateException {
		try {
			DBUpgrader.upgrade();
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		TemplateManagerUtil.init();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Organization> iterator = _organizations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Organization organization = _persistence.create(pk);

		Assert.assertNotNull(organization);

		Assert.assertEquals(organization.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Organization newOrganization = addOrganization();

		_persistence.remove(newOrganization);

		Organization existingOrganization = _persistence.fetchByPrimaryKey(newOrganization.getPrimaryKey());

		Assert.assertNull(existingOrganization);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addOrganization();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Organization newOrganization = _persistence.create(pk);

		newOrganization.setMvccVersion(RandomTestUtil.nextLong());

		newOrganization.setUuid(RandomTestUtil.randomString());

		newOrganization.setCompanyId(RandomTestUtil.nextLong());

		newOrganization.setUserId(RandomTestUtil.nextLong());

		newOrganization.setUserName(RandomTestUtil.randomString());

		newOrganization.setCreateDate(RandomTestUtil.nextDate());

		newOrganization.setModifiedDate(RandomTestUtil.nextDate());

		newOrganization.setParentOrganizationId(RandomTestUtil.nextLong());

		newOrganization.setTreePath(RandomTestUtil.randomString());

		newOrganization.setName(RandomTestUtil.randomString());

		newOrganization.setType(RandomTestUtil.randomString());

		newOrganization.setRecursable(RandomTestUtil.randomBoolean());

		newOrganization.setRegionId(RandomTestUtil.nextLong());

		newOrganization.setCountryId(RandomTestUtil.nextLong());

		newOrganization.setStatusId(RandomTestUtil.nextInt());

		newOrganization.setComments(RandomTestUtil.randomString());

		newOrganization.setLogoId(RandomTestUtil.nextLong());

		_organizations.add(_persistence.update(newOrganization));

		Organization existingOrganization = _persistence.findByPrimaryKey(newOrganization.getPrimaryKey());

		Assert.assertEquals(existingOrganization.getMvccVersion(),
			newOrganization.getMvccVersion());
		Assert.assertEquals(existingOrganization.getUuid(),
			newOrganization.getUuid());
		Assert.assertEquals(existingOrganization.getOrganizationId(),
			newOrganization.getOrganizationId());
		Assert.assertEquals(existingOrganization.getCompanyId(),
			newOrganization.getCompanyId());
		Assert.assertEquals(existingOrganization.getUserId(),
			newOrganization.getUserId());
		Assert.assertEquals(existingOrganization.getUserName(),
			newOrganization.getUserName());
		Assert.assertEquals(Time.getShortTimestamp(
				existingOrganization.getCreateDate()),
			Time.getShortTimestamp(newOrganization.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingOrganization.getModifiedDate()),
			Time.getShortTimestamp(newOrganization.getModifiedDate()));
		Assert.assertEquals(existingOrganization.getParentOrganizationId(),
			newOrganization.getParentOrganizationId());
		Assert.assertEquals(existingOrganization.getTreePath(),
			newOrganization.getTreePath());
		Assert.assertEquals(existingOrganization.getName(),
			newOrganization.getName());
		Assert.assertEquals(existingOrganization.getType(),
			newOrganization.getType());
		Assert.assertEquals(existingOrganization.getRecursable(),
			newOrganization.getRecursable());
		Assert.assertEquals(existingOrganization.getRegionId(),
			newOrganization.getRegionId());
		Assert.assertEquals(existingOrganization.getCountryId(),
			newOrganization.getCountryId());
		Assert.assertEquals(existingOrganization.getStatusId(),
			newOrganization.getStatusId());
		Assert.assertEquals(existingOrganization.getComments(),
			newOrganization.getComments());
		Assert.assertEquals(existingOrganization.getLogoId(),
			newOrganization.getLogoId());
	}

	@Test
	public void testCountByUuid() {
		try {
			_persistence.countByUuid(StringPool.BLANK);

			_persistence.countByUuid(StringPool.NULL);

			_persistence.countByUuid((String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByUuid_C() {
		try {
			_persistence.countByUuid_C(StringPool.BLANK,
				RandomTestUtil.nextLong());

			_persistence.countByUuid_C(StringPool.NULL, 0L);

			_persistence.countByUuid_C((String)null, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByCompanyId() {
		try {
			_persistence.countByCompanyId(RandomTestUtil.nextLong());

			_persistence.countByCompanyId(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByLocations() {
		try {
			_persistence.countByLocations(RandomTestUtil.nextLong());

			_persistence.countByLocations(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByC_P() {
		try {
			_persistence.countByC_P(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong());

			_persistence.countByC_P(0L, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByC_T() {
		try {
			_persistence.countByC_T(RandomTestUtil.nextLong(), StringPool.BLANK);

			_persistence.countByC_T(0L, StringPool.NULL);

			_persistence.countByC_T(0L, (String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByC_N() {
		try {
			_persistence.countByC_N(RandomTestUtil.nextLong(), StringPool.BLANK);

			_persistence.countByC_N(0L, StringPool.NULL);

			_persistence.countByC_N(0L, (String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByO_C_P() {
		try {
			_persistence.countByO_C_P(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

			_persistence.countByO_C_P(0L, 0L, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Organization newOrganization = addOrganization();

		Organization existingOrganization = _persistence.findByPrimaryKey(newOrganization.getPrimaryKey());

		Assert.assertEquals(existingOrganization, newOrganization);
	}

	@Test
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		try {
			_persistence.findByPrimaryKey(pk);

			Assert.fail(
				"Missing entity did not throw NoSuchOrganizationException");
		}
		catch (NoSuchOrganizationException nsee) {
		}
	}

	@Test
	public void testFindAll() throws Exception {
		try {
			_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				getOrderByComparator());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	protected OrderByComparator<Organization> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("Organization_",
			"mvccVersion", true, "uuid", true, "organizationId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "parentOrganizationId", true,
			"treePath", true, "name", true, "type", true, "recursable", true,
			"regionId", true, "countryId", true, "statusId", true, "comments",
			true, "logoId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Organization newOrganization = addOrganization();

		Organization existingOrganization = _persistence.fetchByPrimaryKey(newOrganization.getPrimaryKey());

		Assert.assertEquals(existingOrganization, newOrganization);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Organization missingOrganization = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingOrganization);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		Organization newOrganization1 = addOrganization();
		Organization newOrganization2 = addOrganization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOrganization1.getPrimaryKey());
		primaryKeys.add(newOrganization2.getPrimaryKey());

		Map<Serializable, Organization> organizations = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, organizations.size());
		Assert.assertEquals(newOrganization1,
			organizations.get(newOrganization1.getPrimaryKey()));
		Assert.assertEquals(newOrganization2,
			organizations.get(newOrganization2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Organization> organizations = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(organizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		Organization newOrganization = addOrganization();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOrganization.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Organization> organizations = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, organizations.size());
		Assert.assertEquals(newOrganization,
			organizations.get(newOrganization.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Organization> organizations = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(organizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		Organization newOrganization = addOrganization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOrganization.getPrimaryKey());

		Map<Serializable, Organization> organizations = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, organizations.size());
		Assert.assertEquals(newOrganization,
			organizations.get(newOrganization.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = OrganizationLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod() {
				@Override
				public void performAction(Object object) {
					Organization organization = (Organization)object;

					Assert.assertNotNull(organization);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		Organization newOrganization = addOrganization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Organization.class,
				Organization.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("organizationId",
				newOrganization.getOrganizationId()));

		List<Organization> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Organization existingOrganization = result.get(0);

		Assert.assertEquals(existingOrganization, newOrganization);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Organization.class,
				Organization.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("organizationId",
				RandomTestUtil.nextLong()));

		List<Organization> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		Organization newOrganization = addOrganization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Organization.class,
				Organization.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"organizationId"));

		Object newOrganizationId = newOrganization.getOrganizationId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("organizationId",
				new Object[] { newOrganizationId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingOrganizationId = result.get(0);

		Assert.assertEquals(existingOrganizationId, newOrganizationId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Organization.class,
				Organization.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"organizationId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("organizationId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		if (!PropsValues.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			return;
		}

		Organization newOrganization = addOrganization();

		_persistence.clearCache();

		OrganizationModelImpl existingOrganizationModelImpl = (OrganizationModelImpl)_persistence.findByPrimaryKey(newOrganization.getPrimaryKey());

		Assert.assertEquals(existingOrganizationModelImpl.getCompanyId(),
			existingOrganizationModelImpl.getOriginalCompanyId());
		Assert.assertTrue(Validator.equals(
				existingOrganizationModelImpl.getName(),
				existingOrganizationModelImpl.getOriginalName()));
	}

	protected Organization addOrganization() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Organization organization = _persistence.create(pk);

		organization.setMvccVersion(RandomTestUtil.nextLong());

		organization.setUuid(RandomTestUtil.randomString());

		organization.setCompanyId(RandomTestUtil.nextLong());

		organization.setUserId(RandomTestUtil.nextLong());

		organization.setUserName(RandomTestUtil.randomString());

		organization.setCreateDate(RandomTestUtil.nextDate());

		organization.setModifiedDate(RandomTestUtil.nextDate());

		organization.setParentOrganizationId(RandomTestUtil.nextLong());

		organization.setTreePath(RandomTestUtil.randomString());

		organization.setName(RandomTestUtil.randomString());

		organization.setType(RandomTestUtil.randomString());

		organization.setRecursable(RandomTestUtil.randomBoolean());

		organization.setRegionId(RandomTestUtil.nextLong());

		organization.setCountryId(RandomTestUtil.nextLong());

		organization.setStatusId(RandomTestUtil.nextInt());

		organization.setComments(RandomTestUtil.randomString());

		organization.setLogoId(RandomTestUtil.nextLong());

		_organizations.add(_persistence.update(organization));

		return organization;
	}

	private static Log _log = LogFactoryUtil.getLog(OrganizationPersistenceTest.class);
	private List<Organization> _organizations = new ArrayList<Organization>();
	private OrganizationPersistence _persistence = OrganizationUtil.getPersistence();
}