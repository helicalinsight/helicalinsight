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

package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 26-Jan-15.
 *
 * @author Rajasekhar
 */
public class EfwServiceException extends RuntimeException {
    private static final long serialVersionUID = 6153629785582904829L;

    public EfwServiceException(String message) {
        super(message);
    }

    public EfwServiceException(String message, Exception exception) {
        super(message, exception);
    }

    public EfwServiceException(Throwable throwable) {
        super(throwable);
    }
}
