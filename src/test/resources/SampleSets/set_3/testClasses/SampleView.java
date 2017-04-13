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
import edu.gvsu.prestongarno.annotations.View;
import edu.gvsu.prestongarno.annotations.TranslateView;
import edu.gvsu.prestongarno.sourcegentests.testClasses.SamplePresenter;
import edu.gvsu.prestongarno.sourcegentests.testClasses.SamplePresenter.NumberRequestEvent;


/** **************************************************
 * Dynamic-MVP - edu.gvsu.prestongarno.sourcegentests.SampleSets.set_2 - by Preston Garno on 3/25/17
 * ***************************************************/
@View(SamplePresenter.class)
public class SampleView {
	
	public void onSomeButtonPressed(int parameter) {
		Event e = new NumberRequestEvent(i -> System.out.println(this.toString() + " " + i));
	}
	
	final SamplePresenter.OnNumberProvided numberProvided = number -> { throw new RuntimeException(); };
	final SamplePresenter.OnNumberProvided numberProvided2 = System.out::print;

	public String toString() {
		return new String("This thing shouldn't do anything!");
	}
}
