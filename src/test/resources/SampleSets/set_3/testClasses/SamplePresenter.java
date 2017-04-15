/*
 *       Copyright (c) 2017.  Preston Garno
 *
 *        Licensed under the Apache License, Version 2.0 (the "License");
 *        you may not use this file except in compliance with the License.
 *        You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing, software
 *        distributed under the License is distributed on an "AS IS" BASIS,
 *        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *        See the License for the specific language governing permissions and
 *        limitations under the License.
 */

package edu.gvsu.prestongarno.sourcegentests.testClasses;

import edu.gvsu.prestongarno.Event;
import edu.gvsu.prestongarno.annotations.EventDefinition;
import edu.gvsu.prestongarno.Presenter;
import edu.gvsu.prestongarno.annotations.Callback;
import edu.gvsu.prestongarno.annotations.EventHandler;

/** **************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.sourcegentests.SampleSets.set_3 - by Preston Garno on 3/25/17
 * ***************************************************/
public class SamplePresenter extends Presenter {
	
	public SamplePresenter() {
	}


	@FunctionalInterface
	public interface OnNumberProvided {
		void onReturnNumber(int number);
	}

	/*****************************************
	 * The presenter event handler
	 * @param event the event
	 ****************************************/
	@EventHandler
	public void onRequestNumber(NumberRequestEvent event) {
		// logic here
	}
	
	/*****************************************
	 * Changed the Event class to concrete class to allow for shorter syntax
	 * Event-Callback provided by public final fields or methods
	 ****************************************/
	@Event public final Event<OnNumberProvided> OnSampleEvent = Event.define();

}
