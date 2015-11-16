/*******************************************************************************
 * Copyright 2015 OSGi Alliance
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package osgi.enroute.examples.led.controller.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Dictionary;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

/**
 * Useful Utility Methods
 */
public final class Utils {

	/**
	 * Converts legacy {@link Dictionary} ADT to {@link Map}
	 *
	 * @param dictionary
	 *            The legacy {@link Dictionary} object to transform
	 *
	 * @throws NullPointerException
	 *             if argument is null
	 */
	public static <K, V> Map<K, V> dictionaryToMap(final Dictionary<K, V> dictionary) {
		checkNotNull(dictionary);
		final Iterator<K> keysIter = Iterators.forEnumeration(dictionary.keys());
		final Map<K, V> dict = Maps.toMap(keysIter, dictionary::get);
		return dict;
	}

	/**
	 * Constructor
	 */
	private Utils() {
		// Constructor
	}

}
