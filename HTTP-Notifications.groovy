/**
 *  File: HTTP-Notifications.groovy
 *  Platform: Hubitat
 *  Modification History:
 *            Date        Who            What
 *    v1.0.0  2020-08-16  Brian Johnson  Initial version
 *
 *  Copyright (c) 2020 Brian Johnson
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

def version() {"v1.0.0"}

metadata {
	definition (
		name: "HTTP Notifications",
		namespace: "john3300",
		author: "Brian Johnson",
		description: "Send notification messages to an HTTP URL",
		importUrl: "https://raw.githubusercontent.com/john3300/Hubitat-HTTPNotifications/HTTP-Notifications.groovy"
	) {
		capability "Actuator"
		capability "Notification"
	}

	preferences {
		input name: "httpUrl", type: "text", title: "Notification URL:", required: true
		input name: "queryString", type: "text", title: "Query String:", description: "Use &lt;MSG&gt; as placeholder for message", required: false
	}
}

def deviceNotification(message) {
	log.debug "Sending message [${message}] to [${httpUrl}]"

	def postParams = [
		uri: httpUrl,
		contentType: "text/plain"
	]

	if (queryString) {
		queryString = queryString.replaceAll("<MSG>", URLEncoder.encode(message, "UTF-8"))
		postParams.put("queryString", queryString)
	}

	try {
		httpPost(postParams) { response ->
			if (response.status >= 300) {
				log.error "Received HTTP error ${response.status}!"
			} else {
				log.debug "Message delivered"
			}
		}
	} catch (Exception e) {
		log.error "Error calling httpPost ${e}"
	}
}