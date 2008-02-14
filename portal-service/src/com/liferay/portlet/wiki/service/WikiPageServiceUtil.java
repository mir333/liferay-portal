/**
 * Copyright (c) 2000-2008 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portlet.wiki.service;


/**
 * <a href="WikiPageServiceUtil.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This class provides static methods for the
 * <code>com.liferay.portlet.wiki.service.WikiPageService</code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 *
 * <p>
 * <code>com.liferay.portlet.wiki.service.WikiPageServiceFactory</code>
 * is responsible for the lookup of the bean.
 * </p>
 *
 * @author Brian Wing Shun Chan
 *
 * @see com.liferay.portlet.wiki.service.WikiPageService
 * @see com.liferay.portlet.wiki.service.WikiPageServiceFactory
 *
 */
public class WikiPageServiceUtil {
	public static com.liferay.portlet.wiki.model.WikiPage addPage(long nodeId,
		java.lang.String title, javax.portlet.PortletPreferences prefs,
		com.liferay.portal.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		return wikiPageService.addPage(nodeId, title, prefs, themeDisplay);
	}

	public static void addPageAttachments(long nodeId, java.lang.String title,
		java.util.List files)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		wikiPageService.addPageAttachments(nodeId, title, files);
	}

	public static void deletePage(long nodeId, java.lang.String title)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		wikiPageService.deletePage(nodeId, title);
	}

	public static void deletePageAttachment(long nodeId,
		java.lang.String title, java.lang.String fileName)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		wikiPageService.deletePageAttachment(nodeId, title, fileName);
	}

	public static java.util.List<com.liferay.portlet.wiki.model.WikiPage> getNodePages(
		long nodeId, int max)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		return wikiPageService.getNodePages(nodeId, max);
	}

	public static java.lang.String getNodePagesRSS(long nodeId, int max,
		java.lang.String type, double version, java.lang.String displayStyle,
		java.lang.String feedURL, java.lang.String entryURL)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		return wikiPageService.getNodePagesRSS(nodeId, max, type, version,
			displayStyle, feedURL, entryURL);
	}

	public static com.liferay.portlet.wiki.model.WikiPage getPage(long nodeId,
		java.lang.String title)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		return wikiPageService.getPage(nodeId, title);
	}

	public static com.liferay.portlet.wiki.model.WikiPage getPage(long nodeId,
		java.lang.String title, double version)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		return wikiPageService.getPage(nodeId, title, version);
	}

	public static java.lang.String getPagesRSS(long companyId, long nodeId,
		java.lang.String title, int max, java.lang.String type, double version,
		java.lang.String displayStyle, java.lang.String feedURL,
		java.lang.String entryURL, java.util.Locale locale)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		return wikiPageService.getPagesRSS(companyId, nodeId, title, max, type,
			version, displayStyle, feedURL, entryURL, locale);
	}

	public static void movePage(long nodeId, java.lang.String title,
		java.lang.String newTitle, javax.portlet.PortletPreferences prefs,
		com.liferay.portal.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		wikiPageService.movePage(nodeId, title, newTitle, prefs, themeDisplay);
	}

	public static com.liferay.portlet.wiki.model.WikiPage revertPage(
		long nodeId, java.lang.String title, double version,
		javax.portlet.PortletPreferences prefs,
		com.liferay.portal.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		return wikiPageService.revertPage(nodeId, title, version, prefs,
			themeDisplay);
	}

	public static void subscribePage(long nodeId, java.lang.String title)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		wikiPageService.subscribePage(nodeId, title);
	}

	public static void unsubscribePage(long nodeId, java.lang.String title)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		wikiPageService.unsubscribePage(nodeId, title);
	}

	public static com.liferay.portlet.wiki.model.WikiPage updatePage(
		long nodeId, java.lang.String title, double version,
		java.lang.String content, java.lang.String format,
		java.lang.String redirectTo, java.lang.String[] tagsEntries,
		javax.portlet.PortletPreferences prefs,
		com.liferay.portal.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.PortalException,
			com.liferay.portal.SystemException, java.rmi.RemoteException {
		WikiPageService wikiPageService = WikiPageServiceFactory.getService();

		return wikiPageService.updatePage(nodeId, title, version, content,
			format, redirectTo, tagsEntries, prefs, themeDisplay);
	}
}