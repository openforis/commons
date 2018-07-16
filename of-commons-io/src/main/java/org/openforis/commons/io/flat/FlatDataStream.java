package org.openforis.commons.io.flat;

import java.io.IOException;
import java.util.List;


/**
 * @author G. Miceli
 */
public interface FlatDataStream {
	
	List<Field> getFields();
	
	FlatRecord nextRecord() throws IOException;
	
}
