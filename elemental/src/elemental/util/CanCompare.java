/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package elemental.util;

import elemental.util.ArrayOf;

/**
 * A comparison function which imposes total ordering on a set of objects.
 *
 * @see ArrayOf#sort(CanCompare)
 *
 * @param <T>
 */
@Deprecated
public interface CanCompare<T> {

  /**
   * Compares its two arguments for order.
   * 
   * @param a the first object to be compared
   * @param b the second object to be compared
   * @return a negative integer, zero, or a positive integer as the first
   *         argument is less than, equal to, or greater than the second
   */
  int compare(T a, T b);
}
