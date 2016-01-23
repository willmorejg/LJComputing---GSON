/**
           Copyright 2015, James G. Willmore

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.ljcomputing.gson.config;

import net.ljcomputing.gson.strategy.ExcludeFromJsonAnnotationExclusionStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * GSON Web MVC configurer adapter - overrides message converters.
 * 
 * @author James G. Willmore
 *
 */
@Configuration
@EnableWebMvc
public class GsonWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {

  /** The logger. */
  private static Logger logger = LoggerFactory
      .getLogger(GsonWebMvcConfigurerAdapter.class);

  /**
   * Instantiates a new gson web mvc configurer adapter.
   */
    public GsonWebMvcConfigurerAdapter() {
    logger.info("Initializing {}", this.getClass());
  }

  /**
   * Configure message converters.
   *
   * @param converters the converters
   * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureMessageConverters(java.util.List)
   */
  @Override
  public final void configureMessageConverters(
      final List<HttpMessageConverter<?>> converters) {
    Gson gson = new GsonBuilder()
    .setExclusionStrategies(
        new ExcludeFromJsonAnnotationExclusionStrategy())
    .serializeNulls().create();
    
    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
    
    gsonHttpMessageConverter.setGson(gson);
    
    converters.add(gsonHttpMessageConverter);
  }
}
