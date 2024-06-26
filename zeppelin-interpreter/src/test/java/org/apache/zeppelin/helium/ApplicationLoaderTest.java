/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zeppelin.helium;

import org.apache.commons.io.FileUtils;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.dep.DependencyResolver;
import org.apache.zeppelin.interpreter.InterpreterOutput;
import org.apache.zeppelin.resource.LocalResourcePool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.apache.zeppelin.helium.HeliumPackage.newHeliumPackage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

class ApplicationLoaderTest {
  private File tmpDir;

  @BeforeEach
  void setUp() {
    tmpDir = new File(System.getProperty("java.io.tmpdir") + "/ZeppelinLTest_" +
        System.currentTimeMillis());
    tmpDir.mkdirs();
  }

  @AfterEach
  void tearDown() throws IOException {
    FileUtils.deleteDirectory(tmpDir);
  }

  @Test
  void loadUnloadApplication() throws Exception {
    // given
    LocalResourcePool resourcePool = new LocalResourcePool("pool1");
    DependencyResolver dep =
        new DependencyResolver(tmpDir.getAbsolutePath(), ZeppelinConfiguration.load());
    ApplicationLoader appLoader = new ApplicationLoader(resourcePool, dep);

    HeliumPackage pkg1 = createPackageInfo(MockApplication1.class.getName(), "artifact1");
    ApplicationContext context1 = createContext("note1", "paragraph1", "app1");

    // when load application
    MockApplication1 app = (MockApplication1) ((ClassLoaderApplication)
        appLoader.load(pkg1, context1)).getInnerApplication();

    // then
    assertFalse(app.isUnloaded());
    assertEquals(0, app.getNumRun());

    // when unload
    app.unload();

    // then
    assertTrue(app.isUnloaded());
    assertEquals(0, app.getNumRun());
  }

  public HeliumPackage createPackageInfo(String className, String artifact) {
    HeliumPackage app1 = newHeliumPackage(
        HeliumType.APPLICATION,
        "name1",
        "desc1",
        artifact,
        className,
        new String[][]{{}},
        "license",
        "icon");
    return app1;
  }

  public ApplicationContext createContext(String noteId, String paragraphId, String appInstanceId) {
    ApplicationContext context1 = new ApplicationContext(
        noteId,
        paragraphId,
        appInstanceId,
        null,
        new InterpreterOutput());
    return context1;
  }
}
