/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class BuildInformation {

	static final String MODULE_NAME = "moduleName";
	static final String VERSION = "version";
	static final String BUILD_TIME = "buildtime";
	static final String BUILDER = "builder";
	static final String BUILDER_NAME = "builderName";
	static final String BUILDER_EMAIL = "builderEmail";
	static final String BUILD_HOST = "buildhost";
	static final String BUILD_OS = "buildOs";
	static final String GIT_BRANCH = "gitBranch";
	static final String GIT_COMMIT_ID_DESCRIBE = "gitCommitIdDescribe";
	static final String GIT_COMMIT_ID = "gitCommitId";
	static final String GIT_COMMIT_URL = "gitCommitUrl";
	static final String GIT_COMMIT_TIME = "gitCommitTime";
	static final String GIT_DIRTY = "gitDirty";
	
	private static final String INCONNUE = "Propriété inconnue";
	private static final String MODULE_INCONNU = "Module inconnu";
	
	private final Map<String,Properties> buildInformationPropertiesMap;
	
	BuildInformation() {
		buildInformationPropertiesMap = new HashMap<>();
	}
	
	void addBuildInformation(String moduleName, Properties buildInformationProperties) {
		buildInformationPropertiesMap.put(moduleName, buildInformationProperties);
	}
	
	String getBuildProperty(String moduleName, String property) {
		
		Properties buildInformationProperties = buildInformationPropertiesMap.get(moduleName);
		if (buildInformationProperties != null) {
			return Optional.ofNullable(buildInformationProperties.getProperty(property)).orElse(INCONNUE);
		} else {
			return MODULE_INCONNU;
		}
	}
	
	boolean isGitDirty(String moduleName) {
		Properties buildInformationProperties = buildInformationPropertiesMap.get(moduleName);
		if (buildInformationProperties != null) {
			return Boolean.parseBoolean(buildInformationProperties.getProperty(GIT_DIRTY));
		} else {
			return false;
		}
	}
	
}
