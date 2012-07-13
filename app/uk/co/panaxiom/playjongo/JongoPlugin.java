package uk.co.panaxiom.playjongo;

import play.Application;
import play.Plugin;

public class JongoPlugin extends Plugin {
	
	public JongoPlugin(Application application) {
		
	}
	
	@Override
	public void onStart() {
		PlayJongo.getInstance();
	}
	
	@Override
	public void onStop() {
		PlayJongo.mongo().close();
	}

}
