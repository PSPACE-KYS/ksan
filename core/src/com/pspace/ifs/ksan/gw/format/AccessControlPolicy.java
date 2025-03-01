/*
* Copyright (c) 2021 PSPACE, inc. KSAN Development Team ksan@pspace.co.kr
* KSAN is a suite of free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 
* 3 of the License.  See LICENSE for details
*
* 본 프로그램 및 관련 소스코드, 문서 등 모든 자료는 있는 그대로 제공이 됩니다.
* KSAN 프로젝트의 개발자 및 개발사는 이 프로그램을 사용한 결과에 따른 어떠한 책임도 지지 않습니다.
* KSAN 개발팀은 사전 공지, 허락, 동의 없이 KSAN 개발에 관련된 모든 결과물에 대한 LICENSE 방식을 변경 할 권리가 있습니다.
*/
package com.pspace.ifs.ksan.gw.format;

import java.util.Collection;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;
import com.pspace.ifs.ksan.gw.utils.GWConstants;

/** Represent an Amazon AccessControlPolicy for a container or object. */
// CHECKSTYLE:OFF
public final class AccessControlPolicy {
    @JacksonXmlProperty(localName = GWConstants.XML_OWNER)
	public Owner owner;
    @JacksonXmlProperty(localName = GWConstants.ACCESS_CONTROL_LIST)
	public AccessControlList aclList;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
    	sb.append(GWConstants.LEFT_BRACE);
        if (owner != null) {
            sb.append(GWConstants.ACCESS_OW).append(owner);
        } else {
            sb.append(GWConstants.ACCESS_OW_EMPTY);
        }

        if (aclList != null) {
            sb.append(GWConstants.ACCESS_ACS).append(aclList);
        }
    	
    	return sb.append(GWConstants.RIGHT_BRACE).toString();
    }

    public static final class Owner {
        @JacksonXmlProperty(localName = GWConstants.XML_ID)
		public String id;
        @JacksonXmlProperty(localName = GWConstants.XML_DISPLAY_NAME)
        public String displayName;

        @Override
        public String toString() {
        	StringBuilder sb = new StringBuilder();
            sb.append(GWConstants.CHAR_LEFT_BRACE);
            if (!Strings.isNullOrEmpty(id)) {
                sb.append(GWConstants.ACCESS_ID).append(GWConstants.DOUBLE_QUOTE + id + GWConstants.DOUBLE_QUOTE);
            }
    
            if (!Strings.isNullOrEmpty(displayName)) {
                sb.append(GWConstants.ACCESS_DN).append(GWConstants.DOUBLE_QUOTE).append(displayName).append(GWConstants.DOUBLE_QUOTE);
            }

        	return sb.append(GWConstants.CHAR_RIGHT_BRACE).toString();
        }
    }

    public static final class AccessControlList {
        @JacksonXmlProperty(localName = GWConstants.XML_GRANT)
        @JacksonXmlElementWrapper(useWrapping = false)
		public Collection<Grant> grants;

        @Override
        public String toString() {
        	StringBuilder sb = new StringBuilder();
            sb.append(GWConstants.CHAR_LEFT_BRACE);

            if (grants != null) {
                sb.append(GWConstants.ACCESS_GT).append(grants);
            }
        	
        	return sb.append(GWConstants.CHAR_RIGHT_BRACE).toString();
        }

        public static final class Grant {
            @JacksonXmlProperty(localName = GWConstants.XML_GRANTEE)
			public Grantee grantee;
            @JacksonXmlProperty(localName = GWConstants.XML_PERMISSION)
			public String permission;

            @Override
            public String toString() {
            	StringBuilder sb = new StringBuilder();
                sb.append(GWConstants.CHAR_LEFT_BRACE);

                if(grantee != null) {
                    sb.append(GWConstants.ACCESS_GTE).append(grantee);
                }

                if(!Strings.isNullOrEmpty(permission)) {
                    String tempPermission = "";

                    if(permission.equals(GWConstants.GRANT_FULL_CONTROL)) {
                        tempPermission = GWConstants.GRANT_AB_FC;
                    } else if (permission.equals(GWConstants.GRANT_WRITE)) {
                        tempPermission = GWConstants.GRANT_AB_W;
                    } else if (permission.equals(GWConstants.GRANT_READ)) {
                        tempPermission = GWConstants.GRANT_AB_R;
                    } else if (permission.equals(GWConstants.GRANT_WRITE_ACP)) {
                        tempPermission = GWConstants.GRANT_AB_WA;
                    } else if (permission.equals(GWConstants.GRANT_READ_ACP)) {
                        tempPermission = GWConstants.GRANT_AB_RA;
                    }

                    sb.append(GWConstants.ACCESS_PERM).append(GWConstants.DOUBLE_QUOTE).append(tempPermission).append(GWConstants.DOUBLE_QUOTE);
                }

            	return sb.append(GWConstants.CHAR_RIGHT_BRACE).toString();
            }

            public static final class Grantee {
                @JacksonXmlProperty(namespace=GWConstants.XML_SCHEMA, localName = GWConstants.XML_TYPE, isAttribute = true)
                public String type;
                @JacksonXmlProperty(localName = GWConstants.XML_ID)
				public String id;
                @JacksonXmlProperty(localName = GWConstants.XML_DISPLAY_NAME)
                public String displayName;
                @JacksonXmlProperty(localName = GWConstants.XML_EMAIL_ADDRESS)
                public String emailAddress;
                @JacksonXmlProperty(localName = GWConstants.XML_URI)
				public String uri;

                @Override
                public String toString() {
                	StringBuilder sb = new StringBuilder();
                    sb.append(GWConstants.CHAR_LEFT_BRACE);

                    if(!Strings.isNullOrEmpty(type)) {
                        String tempType = "";
                        if(type.equals(GWConstants.CANONICAL_USER)) {
                            tempType = GWConstants.GRANT_AB_CU;
                        } else if (type.equals(GWConstants.GROUP)) {
                            tempType = GWConstants.GRANT_AB_G;
                        }

                        sb.append(GWConstants.ACCESS_TYPE).append(GWConstants.DOUBLE_QUOTE).append(tempType).append(GWConstants.DOUBLE_QUOTE);
                    }
    
                    if(!Strings.isNullOrEmpty(id)) {
                        sb.append(GWConstants.ACCESS_COMMA_ID).append(GWConstants.DOUBLE_QUOTE).append(id).append(GWConstants.DOUBLE_QUOTE);
                    }

                    if(!Strings.isNullOrEmpty(displayName)) {
                        sb.append(GWConstants.ACCESS_DDN).append(GWConstants.DOUBLE_QUOTE).append(displayName).append(GWConstants.DOUBLE_QUOTE);
                    }

                    if(!Strings.isNullOrEmpty(emailAddress)) {
                        sb.append(GWConstants.ACCESS_EA).append(GWConstants.DOUBLE_QUOTE).append(emailAddress).append(GWConstants.DOUBLE_QUOTE);
                    }

                	if(!Strings.isNullOrEmpty(uri)) {
                        String tempUri = "";
                        
                        if(uri.equals(GWConstants.AWS_GRANT_URI_ALL_USERS)) {
                            tempUri = GWConstants.GRANT_AB_PU;
                        } else if(uri.equals(GWConstants.AWS_GRANT_URI_AUTHENTICATED_USERS)) {
                            tempUri = GWConstants.GRANT_AB_AU;
                        }

                        sb.append(GWConstants.ACCESS_URI).append(GWConstants.DOUBLE_QUOTE).append(tempUri).append(GWConstants.DOUBLE_QUOTE);
                    }
                	
                	return sb.append(GWConstants.CHAR_RIGHT_BRACE).toString();
                }
            }
        }
    }
}
// CHECKSTYLE:ON
