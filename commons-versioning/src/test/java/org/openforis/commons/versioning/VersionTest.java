package org.openforis.commons.versioning;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openforis.commons.versioning.Version.Significance;
import org.openforis.commons.versioning.Version.TestType;

/**
 * 
 * @author S. Ricci
 *
 */
public class VersionTest {
	
	@Test
	public void testConstructor() {
		{
			Version v = new Version( "1.2.3-a14-SNAPSHOT" );
			assertEquals( 1, v.getMajor() );
			assertEquals( 2, v.getMinor() );
			assertEquals( Integer.valueOf(3), v.getBuild() );
			assertEquals( TestType.ALPHA, v.getTestType() );
			assertTrue( v.isAlpha() );
			assertEquals( 1, v.getTestLevel() );
			assertEquals( Integer.valueOf(14), v.getTestVersion() );
			assertTrue( v.isSnapshot() );
		}
		{
			Version v = new Version( "10.20-SNAPSHOT" );
			assertEquals( 10, v.getMajor() );
			assertEquals( 20, v.getMinor() );
			assertNull( v.getBuild() );
			assertNull( v.getTestType() );
			assertEquals( Integer.MAX_VALUE, v.getTestLevel() );
			assertNull( v.getTestVersion() );
			assertTrue( v.isSnapshot() );
		}
		{
			Version v = new Version( "10.20.3" );
			assertEquals( 10, v.getMajor() );
			assertEquals( 20, v.getMinor() );
			assertEquals( Integer.valueOf(3), v.getBuild() );
			assertNull( v.getTestVersion() );
			assertFalse( v.isSnapshot() );
		}
	}
	
	@Test
	public void testCompare() {
		{
			//equals
			Version v1 = new Version("1.2.2-a1-SNAPSHOT");
			Version v2 = new Version("1.2.2-a1-SNAPSHOT");
			assertEquals( 0, v1.compareTo(v2) );
		}
		{
			//v1 less than v2
			Version v1 = new Version("1.2");
			Version v2 = new Version("2.1");
			assertEquals( -1, v1.compareTo(v2) );
		}
		{
			//v1 less than v2
			Version v1 = new Version("1.2.2");
			Version v2 = new Version("1.2.3");
			assertEquals( -1, v1.compareTo(v2) );
		}
		{
			//v1 less than v2
			Version v1 = new Version("1.2-a2");
			Version v2 = new Version("1.2-a3");
			assertEquals( -1, v1.compareTo(v2) );
		}
		{
			//v1 less than v2
			Version v1 = new Version("1.2.1-a2");
			Version v2 = new Version("1.2.1-b1");
			assertEquals( -1, v1.compareTo(v2) );
		}
		{
			//v1 greater than v2
			Version v1 = new Version("1.2.2");
			Version v2 = new Version("1.2.2-SNAPSHOT");
			assertEquals( 1, v1.compareTo(v2) );
		}
		{
			//v1 equals v2 (backwards compatibility)
			Version v1 = new Version("1.2.1-a2");
			Version v2 = new Version("1.2.1-Alpha2");
			assertEquals( 0, v1.compareTo(v2) );
		}
	}

	@Test
	public void testCompareWithSignificance() {
		{
			//equals
			Version v1 = new Version("1.2.3-a1-SNAPSHOT");
			Version v2 = new Version("1.2.2-a1-SNAPSHOT");
			assertEquals( 0, v1.compareTo(v2, Significance.MAJOR) );
			assertEquals( 0, v1.compareTo(v2, Significance.MINOR) );
			assertEquals( 1, v1.compareTo(v2, Significance.BUILD) );
		}
	}
}
