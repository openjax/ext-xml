/* Copyright (c) 2008 OpenJAX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.openjax.xml.sax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A {@link DelegateErrorHandler} contains some other {@link ErrorHandler}, to
 * which it delegates its method calls, possibly transforming the data along the
 * way or providing additional functionality. The class
 * {@link DelegateErrorHandler} itself simply implements all methods of
 * {@link ErrorHandler} with versions that pass all requests to the target
 * {@link ErrorHandler}. Subclasses of {@code DelegateErrorHandler} may further
 * override some of these methods and may also provide additional methods and
 * fields.
 * <p>
 * If the target {@link ErrorHandler} is null, the methods in
 * {@link DelegateErrorHandler} are no-op.
 */
public abstract class DelegateErrorHandler implements ErrorHandler {
  protected volatile ErrorHandler target;

  /**
   * Creates a new {@code DelegateErrorHandler} with the specified {@code target}.
   *
   * @param target The target {@link ErrorHandler} object.
   */
  public DelegateErrorHandler(final ErrorHandler target) {
    this.target = target;
  }

  @Override
  public void warning(final SAXParseException e) throws SAXException {
    if (target != null)
      target.warning(e);
  }

  @Override
  public void error(final SAXParseException e) throws SAXException {
    if (target != null)
      target.error(e);
  }

  @Override
  public void fatalError(final SAXParseException e) throws SAXException {
    if (target != null)
      target.fatalError(e);
  }
}