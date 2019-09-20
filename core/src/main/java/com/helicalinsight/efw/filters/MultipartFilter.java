/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.filters;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

/**
 * The following Filter code is written by the author BAUKE SCHOLTZ.
 * <p/>
 * For applications that are using servlet spec below 3.0, file uploading is
 * made easier by putting all those files as request attributes.
 *
 * @author BalusC (BAUKE SCHOLTZ)
 * @author Rajasekhar(Only minor modifications)
 */
public class MultipartFilter implements Filter {

    /**
     * Max file size for the upload
     */
    private long maxFileSize;

    /**
     * Wrap the given HttpServletRequest with the given parameterMap.
     *
     * @param request      The HttpServletRequest of which the given parameterMap have to
     *                     be wrapped in.
     * @param parameterMap The parameterMap to be wrapped in the given
     *                     HttpServletRequest.
     * @return The HttpServletRequest with the parameterMap wrapped in.
     */
    private static HttpServletRequest wrapRequest(HttpServletRequest request, final Map<String,
            String[]> parameterMap) {
        return new HttpServletRequestWrapper(request) {
            public Map<String, String[]> getParameterMap() {
                return parameterMap;
            }

            public String[] getParameterValues(String name) {
                return parameterMap.get(name);
            }

            public String getParameter(String name) {
                String[] params = getParameterValues(name);
                return (params != null && params.length > 0) ? params[0] : null;
            }

            public Enumeration<String> getParameterNames() {
                return Collections.enumeration(parameterMap.keySet());
            }
        };
    }

    /**
     * Configure the 'maxFileSize' parameter in web.xml.
     *
     * @throws ServletException If 'maxFileSize' parameter value is not numeric.
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        // Configure maxFileSize.
        String maxFileSize = filterConfig.getInitParameter("maxFileSize");
        if (maxFileSize != null) {
            if (!maxFileSize.matches("^\\d+$")) {
                throw new ServletException("MultipartFilter 'maxFileSize' is not numeric.");
            }
            this.maxFileSize = Long.parseLong(maxFileSize);
        }
    }

    /**
     * Check the type request and if it is a HttpServletRequest, then parse the
     * request.
     *
     * @throws ServletException If parsing of the given HttpServletRequest fails.
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws ServletException, IOException {
        // Check type request.
        if (request instanceof HttpServletRequest) {
            // Cast back to HttpServletRequest.
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Parse HttpServletRequest.
            HttpServletRequest parsedRequest = parseRequest(httpRequest);

            // Continue with filter chain.
            chain.doFilter(parsedRequest, response);
        } else {
            // Not a HttpServletRequest.
            chain.doFilter(request, response);
        }
    }

    /**
     * Parse the given HttpServletRequest. If the request is a multipart
     * request, then all multipart request items will be processed, else the
     * request will be returned unchanged. During the processing of all
     * multipart request items, the name and value of each regular form field
     * will be added to the parameterMap of the HttpServletRequest. The name and
     * File object of each form file field will be added as attribute of the
     * given HttpServletRequest. If a FileUploadException has occurred when the
     * file size has exceeded the maximum file size, then the
     * FileUploadException will be added as attribute value instead of the
     * FileItem object.
     *
     * @param request The HttpServletRequest to be checked and parsed as multipart
     *                request.
     * @return The parsed HttpServletRequest.
     * @throws ServletException If parsing of the given HttpServletRequest fails.
     */
    @SuppressWarnings("unchecked")
    // ServletFileUpload#parseRequest() does not return generic type.
    private HttpServletRequest parseRequest(HttpServletRequest request) throws ServletException {

        // Check if the request is actually a multipart/form-data request.
        if (!ServletFileUpload.isMultipartContent(request)) {
            // If not, then return the request unchanged.
            return request;
        }

        // Prepare the multipart request items.
        // I'd rather call the "FileItem" class "MultipartItem" instead or so.
        // What a stupid name ;)
        List<FileItem> multipartItems;

        try {
            // Parse the multipart request items.
            multipartItems = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            // Note: we could use ServletFileUpload#setFileSizeMax() here, but
            // that would throw a
            // FileUploadException immediately without processing the other
            // fields. So we're
            // checking the file size only if the items are already parsed. See
            // processFileField().
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request: " + e.getMessage());
        }

        // Prepare the request parameter map.
        Map<String, String[]> parameterMap = new HashMap<>();

        // Loop through multipart request items.
        for (FileItem multipartItem : multipartItems) {
            if (multipartItem.isFormField()) {
                // Process regular form field (input
                // type="text|radio|checkbox|etc", select, etc).
                processFormField(multipartItem, parameterMap);
            } else {
                // Process form file field (input type="file").
                processFileField(multipartItem, request);
            }
        }

        // Wrap the request with the parameter map which we just created and
        // return it.
        return wrapRequest(request, parameterMap);
    }

    /**
     * Process multipart request item as regular form field. The name and value
     * of each regular form field will be added to the given parameterMap.
     *
     * @param formField    The form field to be processed.
     * @param parameterMap The parameterMap to be used for the HttpServletRequest.
     */
    private void processFormField(FileItem formField, Map<String, String[]> parameterMap) {
        String name = formField.getFieldName();
        String value = formField.getString();
        String[] values = parameterMap.get(name);

        if (values == null) {
            // Not in parameter map yet, so add as new value.
            parameterMap.put(name, new String[]{value});
        } else {
            // Multiple field values, so add new value to existing array.
            int length = values.length;
            String[] newValues = new String[length + 1];
            System.arraycopy(values, 0, newValues, 0, length);
            newValues[length] = value;
            parameterMap.put(name, newValues);
        }
    }

    /**
     * Process multipart request item as file field. The name and FileItem
     * object of each file field will be added as attribute of the given
     * HttpServletRequest. If a FileUploadException has occurred when the file
     * size has exceeded the maximum file size, then the FileUploadException
     * will be added as attribute value instead of the FileItem object.
     *
     * @param fileField The file field to be processed.
     * @param request   The involved HttpServletRequest.
     */
    private void processFileField(FileItem fileField, HttpServletRequest request) {
        if (fileField.getName().length() <= 0) {
            // No file uploaded.
            request.setAttribute(fileField.getFieldName(), null);
        } else if (maxFileSize > 0 && fileField.getSize() > maxFileSize) {
            // File size exceeds maximum file size.
            request.setAttribute(fileField.getFieldName(), new FileUploadException("File size " +
                    "exceeds maximum file size of " + maxFileSize + " bytes."));
            // Immediately delete temporary file to free up memory and/or disk
            // space.
            fileField.delete();
        } else {
            // File uploaded with good size.
            request.setAttribute(fileField.getFieldName(), fileField);
        }
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
    }
}
