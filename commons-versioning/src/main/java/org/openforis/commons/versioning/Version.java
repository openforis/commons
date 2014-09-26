package org.openforis.commons.versioning;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts informations from a version number containing major, minor, revision, alpha and beta numbers.
 * 
 * @author S. Ricci
 * @author D. Wiell
 */
public class Version implements Comparable<Version> {

	private static final String VERSION_PATTERN = "(\\d+)\\.(\\d+)(\\.(\\d+))?(-(a|b|Alpha|Beta)(\\d+)?)?(-SNAPSHOT)?";

	public enum Significance {
		MAJOR, MINOR, BUILD, TEST_LEVEL, TEST_VERSION, SNAPSHOT
	}
	
	public enum TestType {
		ALPHA	("a", "Alpha"), 
		BETA	("b", "Beta");
		
		private String code;
		private String label;

		TestType(String code, String label) {
			this.code = code;
			this.label = label;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getLabel() {
			return label;
		}
		
		public static TestType fromCode(String code) {
			for (TestType testType : values()) {
				if ( testType.code.equals(code) ) {
					return testType;
				}
			}
			return null;
		}

		public static TestType fromLabel(String label) {
			for (TestType testType : values()) {
				if ( testType.label.equals(label) ) {
					return testType;
				}
			}
			return null;
		}
	}
	
	private int major;
	private int minor;
	private Integer build;
	private TestType testType;
	private Integer testVersion;
	private boolean snapshot;
	
	public Version(String value) {
		Matcher m = Pattern.compile(VERSION_PATTERN).matcher(value);
	    if ( ! m.matches() ) {
	        throw new IllegalArgumentException("Malformed version number");
		}
	    this.major = Integer.parseInt(m.group(1));
	    this.minor = Integer.parseInt(m.group(2));
	    
	    if ( m.group(3) != null ) {
	    	this.build = Integer.parseInt(m.group(4));
	    }
	    //Test type (a or b)
	    if ( m.group(5) != null ) {
	    	setTestType(m.group(6));
    		this.testVersion = Integer.parseInt(m.group(7));
	    }
    	this.snapshot = m.group(8) != null;
	}

	public int getMajor() {
		return major;
	}
	
	public void setMajor(int major) {
		this.major = major;
	}
	
	public int getMinor() {
		return minor;
	}
	
	public void setMinor(int minor) {
		this.minor = minor;
	}
	
	public Integer getBuild() {
		return build;
	}
	
	public void setBuild(Integer build) {
		this.build = build;
	}
	
	public TestType getTestType() {
		return testType;
	}
	
	public void setTestType(TestType testType) {
		this.testType = testType;
	}
	
	public void setTestType(String testType) {
		TestType result = TestType.fromCode(testType);
		if ( result == null ) {
			//is testType a label for test type ( Alpha | Beta ) ?
			result = TestType.fromLabel(testType);
		}
		if ( result == null ) {
			throw new IllegalArgumentException("Unexpected test type: " + testType);
		}
		this.testType = result;
	}
	
	public boolean isAlpha() {
		return this.testType == TestType.ALPHA;
	}
	
	public boolean isBeta() {
		return this.testType == TestType.BETA;
	}
	
	public Integer getTestVersion() {
		return testVersion;
	}
	
	public void setTestVersion(Integer testVersion) {
		this.testVersion = testVersion;
	}
	
	public boolean isSnapshot() {
		return snapshot;
	}
	
	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}

	/**
	 * Returns:
	 * 1 if this is a Alpha version
	 * 2 if this is a Beta version
	 * {@link Integer#MAX_VALUE} if this is final release
	 */
	public int getTestLevel() {
		if ( isAlpha() ) {
			return 1;
		} else if ( isBeta() ) {
			return 2;
		} else {
			//final release
			return Integer.MAX_VALUE;
		}
	}
	
	private List<Integer> toList() {
		List<Integer> result = Arrays.asList(
				major, 
				minor, 
				build == null ? 0: build, 
				getTestLevel(), 
				testVersion == null ? Integer.MAX_VALUE: testVersion, 
				snapshot ? 0: 1
		);
		return result;
	}
	
	@Override
	public int compareTo(Version o) {
		return compareTo(o, Significance.SNAPSHOT);
	}
	
	public int compareTo(Version o, Significance significance) {
		List<Integer> list1 = toList().subList(0, significance.ordinal() + 1);
		List<Integer> list2 = o.toList().subList(0, significance.ordinal() + 1);
		for (int i = 0; i < list1.size(); i++) {
			int compareTo = list1.get(i).compareTo(list2.get(i));
			if ( compareTo != 0 ) {
				return compareTo;
			}
		}
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(major);
		sb.append(".");
		sb.append(minor);
		if ( build != null ) {
			sb.append(".");
			sb.append(build);
		}
		if ( testType != null ) {
			sb.append("-");
			sb.append( testType.getCode() );
			if ( testVersion != null ) {
				sb.append(testVersion);
			}
		}
		if ( snapshot ) {
			sb.append("-");
			sb.append("SNAPSHOT");
		}
		return sb.toString();
	}
	
}