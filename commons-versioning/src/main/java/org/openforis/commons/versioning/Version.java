package org.openforis.commons.versioning;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts informations from a version number containing major, minor, revision, alpha and beta numbers.
 * 
 * @author S. Ricci
 */
public class Version implements Comparable<Version> {

	private static final String VERSION_PATTERN = "(\\d+)\\.(\\d+)(\\.(\\d+))?(-(a|b|Alpha|Beta)(\\d+)?)?(-SNAPSHOT)?";

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
	private Integer rev;
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
	    	this.rev = Integer.parseInt(m.group(4));
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
	
	public Integer getRevision() {
		return rev;
	}
	
	public void setRevision(Integer rev) {
		this.rev = rev;
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
	
	@Override
	public int compareTo(Version o) {
		//compare major release
		int result = compareIntegers(major, o.major);
		if ( result == 0 ) {
			//compare minor release
			result = compareIntegers(minor, o.minor);
			if ( result == 0 ) {
				//compare revision
				result = compareIntegers(rev, o.rev);
				if ( result == 0 ) {
					//compare test level
					result = compareIntegers(getTestLevel(), o.getTestLevel());
					if ( result == 0 ) {
						//compare test version
						result = compareIntegers(testVersion, o.getTestVersion());
						if ( result == 0 ) {
							//snapshot version is considered less than final version
							result = - ( Boolean.valueOf(snapshot).compareTo(o.snapshot) );
						}
					}
				}
			}
		}
		return result;
	}
	
	private static int compareIntegers(Integer n1, Integer n2) {
		if ( n1 == null && n2 == null ) {
			return 0;
		} else if ( n1 == null ) {
			return -1;
		} else if ( n2 == null ) {
			return 1;
		} else {
			return n1.compareTo(n2);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(major);
		sb.append(".");
		sb.append(minor);
		if ( rev != null ) {
			sb.append(".");
			sb.append(rev);
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