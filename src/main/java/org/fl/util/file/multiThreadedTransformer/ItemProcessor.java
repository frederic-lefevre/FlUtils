package org.fl.util.file.multiThreadedTransformer;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.fl.util.file.multiThreadedTransformer.ItemProcessor;

public abstract class ItemProcessor {

	protected LinkedBlockingQueue<CharSequence> atypicEntries ;
	
	public abstract ItemProcessor getClone() ;
	public abstract CharSequence processItem(ArrayList<String> currentEntry) ;

	public void setAtypicEntries(LinkedBlockingQueue<CharSequence> ae) {
		atypicEntries = ae ;
	}
		
}
