/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.model.impl;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.bean.ReadOnlyBeanHandler;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.AddressSoap;
import com.liferay.portal.util.PortalUtil;

import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.expando.model.impl.ExpandoBridgeImpl;

import java.io.Serializable;

import java.lang.reflect.Proxy;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <a href="AddressModelImpl.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the Address table in the
 * database.
 * </p>
 *
 * @author    Brian Wing Shun Chan
 * @see       AddressImpl
 * @see       com.liferay.portal.model.Address
 * @see       com.liferay.portal.model.AddressModel
 * @generated
 */
public class AddressModelImpl extends BaseModelImpl<Address> {
	public static final String TABLE_NAME = "Address";
	public static final Object[][] TABLE_COLUMNS = {
			{ "addressId", new Integer(Types.BIGINT) },
			{ "companyId", new Integer(Types.BIGINT) },
			{ "userId", new Integer(Types.BIGINT) },
			{ "userName", new Integer(Types.VARCHAR) },
			{ "createDate", new Integer(Types.TIMESTAMP) },
			{ "modifiedDate", new Integer(Types.TIMESTAMP) },
			{ "classNameId", new Integer(Types.BIGINT) },
			{ "classPK", new Integer(Types.BIGINT) },
			{ "street1", new Integer(Types.VARCHAR) },
			{ "street2", new Integer(Types.VARCHAR) },
			{ "street3", new Integer(Types.VARCHAR) },
			{ "city", new Integer(Types.VARCHAR) },
			{ "zip", new Integer(Types.VARCHAR) },
			{ "regionId", new Integer(Types.BIGINT) },
			{ "countryId", new Integer(Types.BIGINT) },
			{ "typeId", new Integer(Types.INTEGER) },
			{ "mailing", new Integer(Types.BOOLEAN) },
			{ "primary_", new Integer(Types.BOOLEAN) }
		};
	public static final String TABLE_SQL_CREATE = "create table Address (addressId LONG not null primary key,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,classNameId LONG,classPK LONG,street1 VARCHAR(75) null,street2 VARCHAR(75) null,street3 VARCHAR(75) null,city VARCHAR(75) null,zip VARCHAR(75) null,regionId LONG,countryId LONG,typeId INTEGER,mailing BOOLEAN,primary_ BOOLEAN)";
	public static final String TABLE_SQL_DROP = "drop table Address";
	public static final String DATA_SOURCE = "liferayDataSource";
	public static final String SESSION_FACTORY = "liferaySessionFactory";
	public static final String TX_MANAGER = "liferayTransactionManager";
	public static final boolean ENTITY_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.portal.util.PropsUtil.get(
				"value.object.entity.cache.enabled.com.liferay.portal.model.Address"),
			true);
	public static final boolean FINDER_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.portal.util.PropsUtil.get(
				"value.object.finder.cache.enabled.com.liferay.portal.model.Address"),
			true);

	public static Address toModel(AddressSoap soapModel) {
		Address model = new AddressImpl();

		model.setAddressId(soapModel.getAddressId());
		model.setCompanyId(soapModel.getCompanyId());
		model.setUserId(soapModel.getUserId());
		model.setUserName(soapModel.getUserName());
		model.setCreateDate(soapModel.getCreateDate());
		model.setModifiedDate(soapModel.getModifiedDate());
		model.setClassNameId(soapModel.getClassNameId());
		model.setClassPK(soapModel.getClassPK());
		model.setStreet1(soapModel.getStreet1());
		model.setStreet2(soapModel.getStreet2());
		model.setStreet3(soapModel.getStreet3());
		model.setCity(soapModel.getCity());
		model.setZip(soapModel.getZip());
		model.setRegionId(soapModel.getRegionId());
		model.setCountryId(soapModel.getCountryId());
		model.setTypeId(soapModel.getTypeId());
		model.setMailing(soapModel.getMailing());
		model.setPrimary(soapModel.getPrimary());

		return model;
	}

	public static List<Address> toModels(AddressSoap[] soapModels) {
		List<Address> models = new ArrayList<Address>(soapModels.length);

		for (AddressSoap soapModel : soapModels) {
			models.add(toModel(soapModel));
		}

		return models;
	}

	public static final long LOCK_EXPIRATION_TIME = GetterUtil.getLong(com.liferay.portal.util.PropsUtil.get(
				"lock.expiration.time.com.liferay.portal.model.Address"));

	public AddressModelImpl() {
	}

	public long getPrimaryKey() {
		return _addressId;
	}

	public void setPrimaryKey(long pk) {
		setAddressId(pk);
	}

	public Serializable getPrimaryKeyObj() {
		return new Long(_addressId);
	}

	public long getAddressId() {
		return _addressId;
	}

	public void setAddressId(long addressId) {
		_addressId = addressId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public String getUserUuid() throws SystemException {
		return PortalUtil.getUserValue(getUserId(), "uuid", _userUuid);
	}

	public void setUserUuid(String userUuid) {
		_userUuid = userUuid;
	}

	public String getUserName() {
		return GetterUtil.getString(_userName);
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public String getClassName() {
		if (getClassNameId() <= 0) {
			return StringPool.BLANK;
		}

		return PortalUtil.getClassName(getClassNameId());
	}

	public long getClassNameId() {
		return _classNameId;
	}

	public void setClassNameId(long classNameId) {
		_classNameId = classNameId;
	}

	public long getClassPK() {
		return _classPK;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public String getStreet1() {
		return GetterUtil.getString(_street1);
	}

	public void setStreet1(String street1) {
		_street1 = street1;
	}

	public String getStreet2() {
		return GetterUtil.getString(_street2);
	}

	public void setStreet2(String street2) {
		_street2 = street2;
	}

	public String getStreet3() {
		return GetterUtil.getString(_street3);
	}

	public void setStreet3(String street3) {
		_street3 = street3;
	}

	public String getCity() {
		return GetterUtil.getString(_city);
	}

	public void setCity(String city) {
		_city = city;
	}

	public String getZip() {
		return GetterUtil.getString(_zip);
	}

	public void setZip(String zip) {
		_zip = zip;
	}

	public long getRegionId() {
		return _regionId;
	}

	public void setRegionId(long regionId) {
		_regionId = regionId;
	}

	public long getCountryId() {
		return _countryId;
	}

	public void setCountryId(long countryId) {
		_countryId = countryId;
	}

	public int getTypeId() {
		return _typeId;
	}

	public void setTypeId(int typeId) {
		_typeId = typeId;
	}

	public boolean getMailing() {
		return _mailing;
	}

	public boolean isMailing() {
		return _mailing;
	}

	public void setMailing(boolean mailing) {
		_mailing = mailing;
	}

	public boolean getPrimary() {
		return _primary;
	}

	public boolean isPrimary() {
		return _primary;
	}

	public void setPrimary(boolean primary) {
		_primary = primary;
	}

	public Address toEscapedModel() {
		if (isEscapedModel()) {
			return (Address)this;
		}
		else {
			Address model = new AddressImpl();

			model.setNew(isNew());
			model.setEscapedModel(true);

			model.setAddressId(getAddressId());
			model.setCompanyId(getCompanyId());
			model.setUserId(getUserId());
			model.setUserName(HtmlUtil.escape(getUserName()));
			model.setCreateDate(getCreateDate());
			model.setModifiedDate(getModifiedDate());
			model.setClassNameId(getClassNameId());
			model.setClassPK(getClassPK());
			model.setStreet1(HtmlUtil.escape(getStreet1()));
			model.setStreet2(HtmlUtil.escape(getStreet2()));
			model.setStreet3(HtmlUtil.escape(getStreet3()));
			model.setCity(HtmlUtil.escape(getCity()));
			model.setZip(HtmlUtil.escape(getZip()));
			model.setRegionId(getRegionId());
			model.setCountryId(getCountryId());
			model.setTypeId(getTypeId());
			model.setMailing(getMailing());
			model.setPrimary(getPrimary());

			model = (Address)Proxy.newProxyInstance(Address.class.getClassLoader(),
					new Class[] { Address.class },
					new ReadOnlyBeanHandler(model));

			return model;
		}
	}

	public ExpandoBridge getExpandoBridge() {
		if (_expandoBridge == null) {
			_expandoBridge = new ExpandoBridgeImpl(Address.class.getName(),
					getPrimaryKey());
		}

		return _expandoBridge;
	}

	public Object clone() {
		AddressImpl clone = new AddressImpl();

		clone.setAddressId(getAddressId());
		clone.setCompanyId(getCompanyId());
		clone.setUserId(getUserId());
		clone.setUserName(getUserName());
		clone.setCreateDate(getCreateDate());
		clone.setModifiedDate(getModifiedDate());
		clone.setClassNameId(getClassNameId());
		clone.setClassPK(getClassPK());
		clone.setStreet1(getStreet1());
		clone.setStreet2(getStreet2());
		clone.setStreet3(getStreet3());
		clone.setCity(getCity());
		clone.setZip(getZip());
		clone.setRegionId(getRegionId());
		clone.setCountryId(getCountryId());
		clone.setTypeId(getTypeId());
		clone.setMailing(getMailing());
		clone.setPrimary(getPrimary());

		return clone;
	}

	public int compareTo(Address address) {
		int value = 0;

		value = DateUtil.compareTo(getCreateDate(), address.getCreateDate());

		if (value != 0) {
			return value;
		}

		return 0;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		Address address = null;

		try {
			address = (Address)obj;
		}
		catch (ClassCastException cce) {
			return false;
		}

		long pk = address.getPrimaryKey();

		if (getPrimaryKey() == pk) {
			return true;
		}
		else {
			return false;
		}
	}

	public int hashCode() {
		return (int)getPrimaryKey();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{addressId=");
		sb.append(getAddressId());
		sb.append(", companyId=");
		sb.append(getCompanyId());
		sb.append(", userId=");
		sb.append(getUserId());
		sb.append(", userName=");
		sb.append(getUserName());
		sb.append(", createDate=");
		sb.append(getCreateDate());
		sb.append(", modifiedDate=");
		sb.append(getModifiedDate());
		sb.append(", classNameId=");
		sb.append(getClassNameId());
		sb.append(", classPK=");
		sb.append(getClassPK());
		sb.append(", street1=");
		sb.append(getStreet1());
		sb.append(", street2=");
		sb.append(getStreet2());
		sb.append(", street3=");
		sb.append(getStreet3());
		sb.append(", city=");
		sb.append(getCity());
		sb.append(", zip=");
		sb.append(getZip());
		sb.append(", regionId=");
		sb.append(getRegionId());
		sb.append(", countryId=");
		sb.append(getCountryId());
		sb.append(", typeId=");
		sb.append(getTypeId());
		sb.append(", mailing=");
		sb.append(getMailing());
		sb.append(", primary=");
		sb.append(getPrimary());
		sb.append("}");

		return sb.toString();
	}

	public String toXmlString() {
		StringBuilder sb = new StringBuilder();

		sb.append("<model><model-name>");
		sb.append("com.liferay.portal.model.Address");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>addressId</column-name><column-value><![CDATA[");
		sb.append(getAddressId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>companyId</column-name><column-value><![CDATA[");
		sb.append(getCompanyId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userId</column-name><column-value><![CDATA[");
		sb.append(getUserId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userName</column-name><column-value><![CDATA[");
		sb.append(getUserName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>createDate</column-name><column-value><![CDATA[");
		sb.append(getCreateDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>modifiedDate</column-name><column-value><![CDATA[");
		sb.append(getModifiedDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>classNameId</column-name><column-value><![CDATA[");
		sb.append(getClassNameId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>classPK</column-name><column-value><![CDATA[");
		sb.append(getClassPK());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>street1</column-name><column-value><![CDATA[");
		sb.append(getStreet1());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>street2</column-name><column-value><![CDATA[");
		sb.append(getStreet2());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>street3</column-name><column-value><![CDATA[");
		sb.append(getStreet3());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>city</column-name><column-value><![CDATA[");
		sb.append(getCity());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>zip</column-name><column-value><![CDATA[");
		sb.append(getZip());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>regionId</column-name><column-value><![CDATA[");
		sb.append(getRegionId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>countryId</column-name><column-value><![CDATA[");
		sb.append(getCountryId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>typeId</column-name><column-value><![CDATA[");
		sb.append(getTypeId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>mailing</column-name><column-value><![CDATA[");
		sb.append(getMailing());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>primary</column-name><column-value><![CDATA[");
		sb.append(getPrimary());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _addressId;
	private long _companyId;
	private long _userId;
	private String _userUuid;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _classNameId;
	private long _classPK;
	private String _street1;
	private String _street2;
	private String _street3;
	private String _city;
	private String _zip;
	private long _regionId;
	private long _countryId;
	private int _typeId;
	private boolean _mailing;
	private boolean _primary;
	private transient ExpandoBridge _expandoBridge;
}