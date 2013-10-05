/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.core.client;

import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.core.client.js.JsInterface;
import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests JsInterface and JsExport.
 */
@DoNotRunWith(Platform.Devel)
public class JsInterfaceTest extends GWTTestCase {

  @JsInterface(prototype = "$wnd.MyClass")
  interface MyClass {
    static class Prototype implements MyClass {
      public Prototype(int a, int b) { }
      public int x() { return 0; }
      public MyClass x(int a) { return this; }
      public int getY() { return 0; }
      public void setY(int a) { }
      public int sum(int b) { return 0; }
    }

    @JsInterface(prototype = "Document")
    interface Document {
    }

    @JsInterface(prototype = "$wnd.Document")
    interface MainDocument {
    }

    @JsInterface
    interface DocumentLikeJso {
    }

    @JsProperty
    int x();
    @JsProperty MyClass x(int a);
    @JsProperty int getY();
    @JsProperty void setY(int a);
    int sum(int bias);
  }

  static class MyClassImpl extends MyClass.Prototype {
    public static boolean calledFromJsHostPageWindow = false;
    public static boolean calledFromJsModuleWindow = false;

    public MyClassImpl() {
      super(42, 7);
    }

    public int sum(int bias) {
      return super.sum(bias) + 100;
    }

    @JsExport("$wnd.exportedFromJava")
    public static void callMe() {
      calledFromJsHostPageWindow = true;
    }

    @JsExport("exportedFromJava2")
    public static void callMe2() {
      calledFromJsModuleWindow = true;
    }
  }

  @Override
  protected void gwtSetUp() throws Exception {
    ScriptInjector.fromString("function MyClass(a,b) { this.x = a; this.y = b; }\n" +
        "MyClass.prototype.sum = function sum(bias) { return this.x + this.y + bias; }\n")
        .setWindow(ScriptInjector.TOP_WINDOW).inject();
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  public void testSubClass() {
    MyClassImpl mc = new MyClassImpl();
    assertEquals(150, mc.sum(1));

    // test both fluent and non-fluent accessors
    mc.x(-mc.x()).setY(0);
    assertEquals(58, mc.sum(0));

    // Test exported method can be called from JS in host page
    ScriptInjector.fromString("exportedFromJava();").setWindow(ScriptInjector.TOP_WINDOW).inject();
    assertTrue(MyClassImpl.calledFromJsHostPageWindow);

    // Test exported method can be called from JS in module window
    ScriptInjector.fromString("exportedFromJava2();").inject();
    assertTrue(MyClassImpl.calledFromJsModuleWindow);

    // check that instanceof works between frames
    assertTrue(mainDocument() instanceof MyClass.Document);
    assertTrue(localDocument() instanceof MyClass.Document);
    assertTrue(mainDocument() instanceof MyClass.MainDocument);

    // and the casts
    MyClass.Document doc1 = null;
    MyClass.MainDocument doc2 = null;
    MyClass.DocumentLikeJso doc3 = null;
    try {

      assertNotNull(doc1 = (MyClass.Document) mainDocument());
      assertNotNull(doc1 = (MyClass.Document) localDocument());
      assertNotNull(doc2 = (MyClass.MainDocument) mainDocument());
    } catch(ClassCastException cce) {
      fail();
    }
    // check that it doesn't work if $wnd is forced
    assertFalse(localDocument() instanceof MyClass.MainDocument);
    // and casts
    try {
      assertNotNull(doc2 = (MyClass.MainDocument) localDocument());
      fail();
    } catch(ClassCastException cce) {
    }
    // check that JsInterfaces without prototypes can cross-cast like JSOs
    assertTrue(mainDocument() instanceof MyClass.DocumentLikeJso);
    assertTrue(localDocument() instanceof MyClass.DocumentLikeJso);

    // and casts
    try {
      assertNotNull(doc3 = (MyClass.DocumentLikeJso) mainDocument());
      assertNotNull(doc3 = (MyClass.DocumentLikeJso) localDocument());
    } catch(ClassCastException cce) {
      fail();
    }

    // prevent compiler pruning
    assertNotNull(doc1);
    assertNotNull(doc2);
    assertNotNull(doc3);
  }

  private native Object localDocument() /*-{
    return document;
  }-*/;

  private native Object mainDocument() /*-{
    return $doc;
  }-*/;
}