package com.helicalinsight.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.efw.exceptions.MalformedJsonException;

public class GsonUtility {

	public static JsonObject optJsonObject(JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonObject()) {
				return jsonElement.getAsJsonObject();
			}
		}
		return null;
	}

	public static String optString(JsonObject jsonObject, String key) {
		
		if ( jsonObject == null ) return null;
		
		if (jsonObject.has(key)) {
            JsonElement element = jsonObject.get(key);
            if (element.isJsonPrimitive()) {
                return element.getAsString();
            } 
            else if(element.isJsonNull()) {
            	return "";
            }
            else {
                return element.toString();
            }
        } else {
            return "";
        }
	}
	public static JsonArray optJsonArray(JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonArray()) {
				return jsonElement.getAsJsonArray();
			}
		}
		return null;
	}
	public static String optStringValue(JsonObject jsonObject, String key, String defaultValue) {
		if (jsonObject.has(key)) {
            JsonElement element = jsonObject.get(key);
            if (element.isJsonPrimitive()) {
                return element.getAsString();
            } 
            else if(element.isJsonNull()) {
            	return defaultValue;
            }
            else {
                return element.toString();
            }
        } else {
            return defaultValue;
        }
	}
	
	public static boolean optBooleanValue(JsonObject jsonObject, String key, boolean defaultValue) {
		if (jsonObject.has(key)) {
            JsonElement jsonElement = jsonObject.get(key);

            if (jsonElement.isJsonPrimitive()) {
                if (jsonElement.getAsJsonPrimitive().isBoolean()) {
                    return jsonElement.getAsBoolean();
                } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsString().equals("true") || jsonElement.getAsString().equals("false")) {
                    return Boolean.parseBoolean(jsonElement.getAsString());
                }
            }
        }

        return defaultValue;
	}

	public static boolean optBoolean(JsonObject jsonObject, String key) {
		if (jsonObject.has(key)) {
            if (jsonObject.get(key).isJsonPrimitive()) {
                if (jsonObject.get(key).getAsJsonPrimitive().isBoolean()) {
                    return jsonObject.get(key).getAsBoolean();
                } else if (jsonObject.get(key).isJsonPrimitive() && jsonObject.get(key).getAsString().equalsIgnoreCase("true")
                		|| jsonObject.get(key).getAsString().equalsIgnoreCase("false")) {
                    return Boolean.parseBoolean(jsonObject.get(key).getAsString());
                }
            }
        }
        return false;
	}
	public static String optStringFromJsonArray(JsonArray jsonArray, int index) {
	    if (jsonArray != null && index >= 0 && index < jsonArray.size()) {
	    	if(jsonArray.get(index).isJsonPrimitive()) {
	    		return jsonArray.get(index).getAsString();
	    	}else {
	    		return jsonArray.get(index).toString();
	    	}	
	    }
	    return null; // Invalid input or index out of bounds
	}

	public static int optInt(JsonObject jsonObject, String key) {
		if (jsonObject.has(key)) {
            JsonPrimitive primitive = jsonObject.getAsJsonPrimitive(key);
            if (primitive.isNumber()) {
                return primitive.getAsInt();
            } else if (primitive.isString()) {
                try {
                    return Integer.parseInt(primitive.getAsString());
                } catch (NumberFormatException e) {
                    
                }
            }
        }
        return 0; 
	}

	public static int optIntValue(JsonObject jsonObject, String key, int defaultValue) {
		if (jsonObject.has(key)) {
            JsonElement jsonElement = jsonObject.get(key);

            if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
                return jsonElement.getAsNumber().intValue();
            } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                try {
                    return Integer.parseInt(jsonElement.getAsString());
                } catch (NumberFormatException e) {
                    // Handle parsing exception, return default value
                }
            }
        }

        return defaultValue;
    
	}
	
	public static Double optDouble(JsonObject jsonObject, String key) {
        if (jsonObject != null && jsonObject.has(key)) {
            JsonElement jsonElement = jsonObject.get(key);

            if (jsonElement.isJsonPrimitive()) {
                if (jsonElement.getAsJsonPrimitive().isNumber()) {
                    // If it's already a number, return the double value
                    return jsonElement.getAsJsonPrimitive().getAsDouble();
                } else if (jsonElement.getAsJsonPrimitive().isString()) {
                    // Attempt to parse the string as a number
                    try {
                        return Double.parseDouble(jsonElement.getAsString());
                    } catch (NumberFormatException e) {
                        // Return NaN if parsing fails
                        return Double.NaN;
                    }
                }
            }
        }
        return Double.NaN;
    }
	
	/**
	 * Converts a {@link JsonElement} into a {@link Collection} for Jasper parameters
	 * declared as {@code java.util.Collection}. Arrays become lists; primitives become
	 * singleton lists; other shapes yield an empty list.
	 */
	public static Collection<Object> jsonElementToCollection(JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return Collections.emptyList();
		}
		if (element.isJsonArray()) {
			return jsonArrayToList(element.getAsJsonArray());
		}
		if (element.isJsonPrimitive()) {
			return Collections.singletonList(jsonElementToObject(element));
		}
		return Collections.emptyList();
	}

	public static List<Object> jsonArrayToList(JsonArray array) {
		List<Object> list = new ArrayList<>(array.size());
		for (JsonElement element : array) {
			list.add(jsonElementToObject(element));
		}
		return list;
	}

	public static Object jsonElementToObject(JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return null;
		}
		if (element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if (primitive.isBoolean()) {
				return primitive.getAsBoolean();
			}
			if (primitive.isNumber()) {
				return primitive.getAsNumber();
			}
			return primitive.getAsString();
		}
		if (element.isJsonArray()) {
			return jsonArrayToList(element.getAsJsonArray());
		}
		return element.toString();
	}

	/**
	 * Replaces a key in {@code jsonObject} with a Gson representation of {@code value}.
	 * Supports strings, numbers, booleans, {@link JsonElement}, and collections/arrays
	 * (stored as {@link JsonArray}) for EFWD query parameter substitution.
	 */
	public static void setMappedValue(JsonObject jsonObject, String key, Object value) {
		if (value instanceof JsonElement jsonElement) {
			jsonObject.add(key, jsonElement);
		} else if (value instanceof String stringValue) {
			jsonObject.addProperty(key, stringValue);
		} else if (value instanceof Number numberValue) {
			jsonObject.add(key, new JsonPrimitive(numberValue));
		} else if (value instanceof Boolean booleanValue) {
			jsonObject.addProperty(key, booleanValue);
		} else if (value instanceof Character characterValue) {
			jsonObject.addProperty(key, characterValue);
		} else {
			jsonObject.add(key, new Gson().toJsonTree(value));
		}
	}

	public static JsonObject accumulate(JsonObject jsonObject, String key, Object value) {
		Gson gson = new GsonBuilder().serializeNulls().create();

        if (jsonObject.has(key)) {
            JsonElement existingValue = jsonObject.get(key);

            // If it's a JsonArray and has existing values, append the new value
            if (existingValue.isJsonArray()) {
                JsonArray jsonArray = existingValue.getAsJsonArray();
                jsonArray.add(gson.toJsonTree(value));
            } else {
                // Convert the existing value into a JsonArray and append the new value
                JsonArray newArray = new JsonArray();
                newArray.add(existingValue);
                newArray.add(gson.toJsonTree(value));
                jsonObject.add(key, newArray);
            }
        } else {
            // If the key doesn't exist, add the value

            
            if (value instanceof String) {
				jsonObject.addProperty(key,(String)value);
			} else if (value instanceof JsonObject) {
				jsonObject.add(key,(JsonObject) value);
			} else if (value instanceof JsonArray) {
				jsonObject.add(key,(JsonArray) value);
			} else if (value instanceof Map) {
				jsonObject.add(key,gson.toJsonTree(value).getAsJsonObject());
			} else if (value instanceof List) {
				jsonObject.add(key,gson.toJsonTree(value).getAsJsonArray());
			} else if (value instanceof HashSet) {
				jsonObject.add(key,gson.toJsonTree(value).getAsJsonArray());
			} else if(value instanceof Number numberValue){
				jsonObject.add(key, new JsonPrimitive(numberValue));
			}else {
				jsonObject.add(key,(JsonElement) value);
			}
        }

        return jsonObject;

	}
	
	public static JsonObject accumulateInt(JsonObject jsonObject, String key, int value) {
		if (key == null) {
            // Handle the case where the key is null
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (jsonObject.has(key)) {
            JsonElement element = jsonObject.get(key);

            if (element.isJsonArray()) {
                // If there is already a JSONArray, append the new value to it
                JsonArray jsonArray = element.getAsJsonArray();
                jsonArray.add(value);
            } else {
                // If there is already an object, create a new JSONArray and add both values
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(element);
                jsonArray.add(value);
                jsonObject.add(key, jsonArray);
            }
        } else {
            // If the key is not present, add the new value as a single value or in a new JSONArray
            jsonObject.addProperty(key, value);
        }

        return jsonObject;
    }

	public static JsonObject accumulateBoolean(JsonObject jsonObject, String key, boolean value) {
        if (jsonObject == null || key == null) {
            throw new IllegalArgumentException("JSON object or key is null");
        }

        if (jsonObject.has(key)) {
            // Key already exists in the JsonObject
            JsonElement existingValue = jsonObject.get(key);

            if (existingValue.isJsonArray()) {
                // If there's already a JsonArray under the key, append the new boolean value
                JsonArray jsonArray = existingValue.getAsJsonArray();
                jsonArray.add(value);
            } else {
                // If there's a non-JsonArray value, create a new JsonArray and append both values
                JsonArray newArray = new JsonArray();
                newArray.add(existingValue);
                newArray.add(value);
                jsonObject.add(key, newArray);
            }
        } else {
            // Key doesn't exist, simply put the boolean value
            jsonObject.addProperty(key, value);
        }

        return jsonObject;
    }

	public static void accumulateAll(JsonObject jsonObject, Object map) {
		Gson gson = new GsonBuilder().serializeNulls().create();
		JsonObject mapToJsonObject = gson.toJsonTree(map).getAsJsonObject();
		mapToJsonObject.entrySet().stream().forEach(entry -> jsonObject.add(entry.getKey(), entry.getValue()));
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T extends JsonElement > T parseString(String jsonString, Class<T> t ) {
		if ( t  == JsonArray.class) {
			return (T) JsonParser.parseString(jsonString).getAsJsonArray();
		}
		else if ( t ==  JsonObject.class) {
			return (T) JsonParser.parseString(jsonString).getAsJsonObject();
		}
		throw new MalformedJsonException("Invalid JSON format: Unable to parse the input string.");
	}
	
	public static JsonObject getJsonObjectIfNotEmpty(JsonObject jsonObject, String key) {
		JsonObject object =  optJsonObject(jsonObject, key);
		if ( object != null &&  !object.keySet().isEmpty()) {
			return object;
		}
		return null;
	}
	
	public static JsonElement getByPath(JsonObject json, String path) {
		String[] parts = path.split("\\.");
		JsonElement current = json;
		for (String key : parts) {
			if (current == null || !current.isJsonObject())
				return null;
			current = current.getAsJsonObject().get(key);
		}
		return current;
	}
	

	public static <T> T deserialize(JsonObject object, Class<T> clazz) {
		return new Gson().fromJson(object, clazz);
	}
	
	public static JsonObject optJsonObjectOrEmpty(JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonObject()) {
				return jsonElement.getAsJsonObject();
			}
		}
		return new JsonObject();
	}
}
