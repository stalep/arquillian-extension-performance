/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.glassfish;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.jboss.arquillian.impl.DynamicServiceLoader;
import org.jboss.arquillian.impl.XmlConfigurationBuilder;
import org.jboss.arquillian.impl.context.SuiteContext;
import org.jboss.arquillian.spi.Context;
import org.jboss.arquillian.spi.DeployableContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * GlassFishEmbeddedContainerTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class GlassFishEmbeddedContainerTestCase 
{
   private DeployableContainer container;
   
   private Context context = new SuiteContext(new DynamicServiceLoader());
   
   private WebArchive war = ShrinkWrap.create("test.war", WebArchive.class)
                              .addClass(TestServlet.class);

   @Before
   public void startup() throws Exception
   {
      container = new GlassFishEmbeddedContainer();
      container.setup(context, new XmlConfigurationBuilder().build());
      container.start(context);
      container.deploy(context, war);
   }

   @After
   public void shutdown() throws Exception
   {
      container.undeploy(context, war);
      container.stop(context);
   }

   @Test
   public void shouldBeAbleToDeployWebArchive() throws Exception
   {
      String body = readAllAndClose(new URL("http://localhost:8080/test/Test").openStream());
      
      Assert.assertEquals(
            "Verify that the servlet was deployed and returns expected result",
            "hello",
            body);
   }
   
   private String readAllAndClose(InputStream is) throws Exception 
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try
      {
         int read;
         while( (read = is.read()) != -1)
         {
            out.write(read);
         }
      }
      finally 
      {
         try { is.close(); } catch (Exception e) { }
      }
      return out.toString();
   }
}