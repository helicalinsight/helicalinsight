package com.helicalinsight.efw.jasperintegration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FilenameUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.TaskExecutorService;

public class HCRCustomScriptCompiler {
	
	
	private static final Logger logger = LoggerFactory.getLogger(HCRCustomScriptCompiler.class);
	
	private static final String SCRIPT_FILE_NAME = "script.js";
	private static final String BUILD_FILE_NAME = "build.js";
	private static final String OUT_FILE_NAME = "script.min.js";
	private static final String SYSTEM_DIR = ApplicationProperties.getInstance().getSystemDirectory();
	private static final String COMPILER_JS = "r.js";
	private static final String DEPS_FILE_PATH = String.join(File.separator, SYSTEM_DIR ,HCRUtils.HCR_SCRIPTS_PATH ,"dependencies");
	
	
	
	@SuppressWarnings("unchecked")
	public String compile(String js ,String moduleName, String deps, String dir) {
		
		File container = new File(dir);
		boolean isDirCreated = container.mkdir();
		
		if ( !isDirCreated) {
			throw new EfwServiceException("Could not compile the code, error occurred while creating  directory : " + dir);
		}
		
		String rootDirAbsPath = container.getAbsolutePath();
		
		String buildFilePath =  Paths.get(rootDirAbsPath , BUILD_FILE_NAME).toString();
		
		String rJsFilePath =    Paths.get(SYSTEM_DIR, HCRUtils.HCR_SCRIPTS_PATH, COMPILER_JS).toString();

		HCRUtils.writeToFile(js, dir, SCRIPT_FILE_NAME);
		
		JsonObject dependencies = 	GsonUtility.parseString(deps, JsonObject.class);
		
		copyDependencies(dependencies,  rootDirAbsPath);
		
		String  buildFileAsString = makeBuildFile(rootDirAbsPath,dependencies, moduleName);
		
		HCRUtils.writeToFile(buildFileAsString, dir, BUILD_FILE_NAME);
		
		TaskExecutorService executorService = ApplicationContextAccessor.getBean(TaskExecutorService.class);
		
		try {
			Callable<Boolean> task = isNodeInstalled() ? new NodeCompiler(rJsFilePath, buildFilePath)
					: new RhinoCompiler(rJsFilePath, buildFilePath);
			
			String requestId = RequestContext.get();
			logger.debug("Request ID : {}", requestId);
			long start = System.currentTimeMillis();
			Future<Boolean> future =  (Future<Boolean>) executorService.submit(task, requestId);
			try {
				// FIXME : Make it configurable
				Boolean success = future.get(180,TimeUnit.SECONDS);
				if (success != null && success ) {
					double end = (System.currentTimeMillis() - start)/1000.0;
					logger.info("Compilation took : {} s", end);
					return String.join(File.separator, container.getAbsolutePath(), OUT_FILE_NAME);
				}
				else {
					throw new EfwServiceException("Compilation failed.");
				}
			}
			catch (TimeoutException ex) {
				future.cancel(true);
				throw new EfwServiceException("Compilation timed out.");
			}
			catch (InterruptedException e) {
				future.cancel(true);
				Thread.currentThread().interrupt();
				throw new EfwServiceException("Compilation interrupted.");
			}
		}
		catch (CancellationException e) {
		    logger.warn("Compilation was cancelled.");
		    throw new EfwServiceException("Compilation cancelled.", e);
		} 
		catch (Exception e) {
			logger.error("Error occurred while compiling  : {}", e.getMessage());
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			throw new EfwServiceException("Compilation error : " , e);
		} 
	}
	
	private  boolean isNodeInstalled() {
        try {
            ProcessBuilder builder = new ProcessBuilder("node", "-v");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String output = reader.readLine();
                if (output != null && output.startsWith("v")) {
                	logger.info("Identified Node version : " + output);
                    return true;
                }
            }
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
	
	private String makeBuildFile(String dir, JsonObject dependencies, String moduleName) {
		
		dependencies.addProperty(moduleName, FilenameUtils.getBaseName(SCRIPT_FILE_NAME));
		
		JsonObject buildJson = new JsonObject();
		buildJson.addProperty("baseUrl", dir);
		buildJson.add("paths", dependencies);
		buildJson.addProperty("name", moduleName);
		buildJson.addProperty("out", OUT_FILE_NAME);
		buildJson.addProperty("optimize", "none");
		
		StringBuilder buildFile = new StringBuilder("(");
		buildFile.append(buildJson.toString());
		buildFile.append(")");
		
		logger.info("Build file  : {}" , buildFile.toString());
		
		return buildFile.toString();
	}
	
	
	private void copyDependencies(JsonObject dependencyObj, String target) {
		
		dependencyObj.keySet().forEach(key -> {
			
			String depName = dependencyObj.get(key).getAsString();
			String depFilePath = Paths.get(DEPS_FILE_PATH , depName + JsonUtils.JS_EXTENSION ).toString();
			String targetFileName = Paths.get(target , depName + JsonUtils.JS_EXTENSION).toString();
			try {
				Files.copy(new File(depFilePath), new File(targetFileName));
			}
			catch (IOException  e) {
				logger.error("Error occurred while copying dependencies : {}" , e);
			}
		});
	}
	
	
	// TODO : If new compiler requirement arises, split this code into multiple classes
	
	private static class RhinoCompiler implements Callable<Boolean> {

		private final String rjsPath;
		private final String buildFilePath;

		private RhinoCompiler(String rjsPath, String buildFilePath) {
			this.rjsPath = rjsPath;
			this.buildFilePath = buildFilePath;
		}

		@Override
		public Boolean call()  {
			
			// context with interruption observer.
			Context cx = new ContextFactory() {
			    @Override
			    protected Context makeContext() {
			        Context cx = super.makeContext();
			        cx.setInstructionObserverThreshold(10000);
			        return cx;
			    }

			    @Override
			    protected void observeInstructionCount(Context cx, int instructionCount) {
			        if (Thread.currentThread().isInterrupted()) {
			            throw new Error("Execution interrupted");
			        }
			    }
			}.enterContext();

			
			try {
				
				logger.info("Node.js was not detected. Falling back to the Rhino compiler.");
				logger.warn("Script compilation and optimization may be slower with Rhino. For optimal performance, please use Node.js.");
				
				Scriptable scope = cx.initStandardObjects();

				// --- Mock print function ---
				ScriptableObject.putProperty(scope, "print", new BaseFunction() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
						for (Object arg : args) {
							logger.debug(Context.toString(arg));
						}
						return Context.getUndefinedValue();
					}
				});

				// --- Mock console.log ---
				ScriptableObject console = (ScriptableObject) cx.newObject(scope);
				ScriptableObject.putProperty(console, "log", new BaseFunction() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
						for (Object arg : args) {
							logger.debug(Context.toString(arg));
						}
						return Context.getUndefinedValue();
					}
				});
				ScriptableObject.putProperty(scope, "console", console);

				// --- Define 'arguments' as empty array ---
				Object[] emptyArgs = new Object[0];
				ScriptableObject.putProperty(scope, "arguments", cx.newArray(scope, emptyArgs));

				// --- Define 'readFully' function ---
				String readFullyFn = "" + "function readFully(file) { "
						+ "  var FileReader = Packages.java.io.FileReader; "
						+ "  var BufferedReader = Packages.java.io.BufferedReader; "
						+ "  var br = new BufferedReader(new FileReader(file)); "
						+ "  var sb = new java.lang.StringBuilder(); " + "  var line = null; "
						+ "  while ((line = br.readLine()) != null) { " + "    sb.append(line); sb.append('\\n'); "
						+ "  } " + "  br.close(); " + "  return sb.toString(); " + "}";

				cx.evaluateString(scope, readFullyFn, "readFullyDef", 1, null);

				// --- Define minimal 'process' object with exit method ---
				ScriptableObject process = (ScriptableObject) cx.newObject(scope);
				ScriptableObject.putProperty(process, "exit", new BaseFunction() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
						int status = 0;
						if (args.length > 0 && args[0] instanceof Number) {
							status = ((Number) args[0]).intValue();
						}
						logger.debug("Process exit called with status {}", status);
						// Do not actually exit JVM here to avoid stopping the Java app.
						return Context.getUndefinedValue();
					}
				});
				ScriptableObject.putProperty(scope, "process", process);

				Object[] jsArgs = new Object[] { "-o", this.buildFilePath };
				Scriptable jsArgsArray = cx.newArray(scope, jsArgs);
				ScriptableObject.putProperty(scope, "arguments", jsArgsArray);

				cx.evaluateReader(scope, new FileReader(rjsPath), rjsPath, 1, null);

				logger.info("Compilation is done.");
				return true;
			} catch (Exception e) {
				logger.warn("Rhino execution interrupted or failed.");

				if (Thread.currentThread().isInterrupted()) {
					logger.warn("Compilation cancelled.");
				}
				return false;
	        }
	        finally {
	            Context.exit();
	        }
		}
	}
	
	
	private static class NodeCompiler implements  Callable<Boolean> {
	
		private final String rjsPath;
		private final String buildFilePath;
		
		
		private NodeCompiler(String rjsPath, String buildFilePath) {
			this.rjsPath = rjsPath;
			this.buildFilePath = buildFilePath;
		}

		
		@Override
		public Boolean call() throws Exception {
			ProcessBuilder builder = new ProcessBuilder("node", this.rjsPath,  "-o", this.buildFilePath);
	    	builder.redirectErrorStream(true);
	    	Process process =  builder.start();
	    	
	    	try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	               logger.info(line);
	            }
	    	
	        int exitCode = process.waitFor();
	        return exitCode == 0;
	    	}
	    	catch (InterruptedException e) {
	    		logger.debug("Thread interrupted. Killing Node compilation process.");
	    		
	    		if ( process.isAlive()) {
	    			process.destroyForcibly();
	    		}
	    		Thread.currentThread().interrupt();
	    		return false;
			}
		}
		
	}
}
