# Get user_access_token

OAuth token API for acquiring the <code>user_access_token</code> and <code>refresh_token</code>. The <code>user_access_token</code> serves as the user's access credential, which allows API calls to be made on behalf of the user. The <code>refresh_token</code> is used to obtain a new <code>user_access_token</code>.

- You need to obtain an authorization code before getting the `user_access_token`. For more details, see [Get Authorization Code](https://open.feishu.cn/document/common-capabilities/sso/api/obtain-oauth-code). Please note that the authorization code is valid for 5 minutes and can only be used once.
- During user authorization, the user must have [access to the application](https://open.feishu.cn/document/home/introduction-to-scope-and-authorization/availability). Otherwise, calling this API will return error code 20010.
- The obtained user_access_token has an expiration period. For instructions on how to refresh the <code>user_access_token</code>, see [Refresh user_access_token](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/authentication-management/access-token/refresh-user-access-token).
- If you need to obtain user information, see [Get User Information](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/authen-v1/user_info/get).
**Notice**：This API implementation adheres to [RFC 6749 - The OAuth 2.0 Authorization Framework](https://datatracker.ietf.org/doc/html/rfc6749). You can integrate using [standard OAuth client libraries](https://oauth.net/code/) (**recommended**).

## Request

Basic | &nbsp;
---|---
HTTP URL | https://open.feishu.cn/open-apis/authen/v2/oauth/token
HTTP Method | POST
API Rate Limit | [1000 times/minute, 50 times/second](https://open.feishu.cn/document/ukTMukTMukTM/uUzN04SN3QjL1cDN)
Supported Application Types | Custom App、Store App
Permission Requirements<br>**Permissions required to call this API. Activating any one of these permissions will suffice for making the call.<br>** | None
Field Permission Requirements | `refresh_token` and `refresh_token_expires_in` fields are only returned when the following permission is granted:<br>offline_access(offline_access)

### Request Headers

Name | Type | Required | Description
---|---|---|---
Content-Type | string | Yes | Type of request body.<br>**Fixed value:** `application/json; charset=utf-8`

### Request Body

Name | Type | Required | Description
---|---|---|---
grant_type | string | Yes | Authorization type.<br>**Fixed Value:** `authorization_code`
client_id | string | Yes | App ID of the application. How to obtain App ID and App Secret:<br>1. Log in to [Developer Consle](https://open.feishu.cn/app).<br>2. Enter the application details page, and click **Credentials & Basic Info** on the left navigation bar.<br>3. In the **Credentials** area, obtain and save the **App ID** and **App Secret**.<br>**Example Value:** `cli_a5ca35a685b0x26e`
client_secret | string | Yes | App Secret of the application. How to obtain App ID and App Secret:<br>1. Log in to [Developer Consle](https://open.feishu.cn/app).<br>2. Enter the application details page, and click **Credentials & Basic Info** on the left navigation bar.<br>3. In the **Credentials** area, obtain and save the **App ID** and **App Secret**.<br>**Example Value:** `baBqE5um9LbFGDy3X7LcfxQX1sqpXlwy`
code | string | Yes | Authorization code, detailed in [Get Authorization Code](https:/https://open.feishu.cn/document/common-capabilities/sso/api/obtain-oauth-code).<br>**Example Value:** `a61hb967bd094dge949h79bbexd16dfe`
redirect_uri | string | No | The application callback URL appended when constructing the authorization page URL. Not required for gadget authorization scenarios.<br>**Example Value:** `https://example.com/api/oauth/callback`
code_verifier | string | No | A randomly generated string used for PKCE (Proof Key for Code Exchange) process generated locally before initiating the authorization. This value is required when using PKCE.<br>For a detailed description of PKCE, please refer to [RFC 7636 - Proof Key for Code Exchange by OAuth Public Clients](https://datatracker.ietf.org/doc/html/rfc7636).<br>**Length Constraints:** Minimum 43 characters, maximum 128 characters<br>**Allowed Character Set:** [A-Z] / [a-z] / [0-9] / "-" / "." / "_" / "~"<br>**Example Value:** `TxYmzM4PHLBlqm5NtnCmwxMH8mFlRWl_ipie3O0aVzo`
scope | string | No | This parameter is used to reduce the permission scope of `user_access_token`.<br>For example:<br>1. When [obtaining an authorization code](https://open.feishu.cn/document/common-capabilities/sso/api/obtain-oauth-code), you can authorize three permissions using the `scope` parameter: `contact:user.base:readonly contact:contact.base:readonly contact:user.employee:readonly`.<br>2. In the current API, you can pass `contact:user.base:readonly` through the `scope` parameter to reduce the permissions of `user_access_token` to only `contact:user.base:readonly`.<br>**Note:**<br>- If this parameter is not specified, the generated `user_access_token` will include all the permissions authorized by the user.<br>- This parameter must not contain duplicate permissions; otherwise, the API call will result in an error with error code 20067.<br>- This parameter must not contain unauthorized permissions (i.e., permissions that were not within the scope authorized by the user when [obtaining the authorization code](https://open.feishu.cn/document/common-capabilities/sso/api/obtain-oauth-code)); otherwise, the API call will result in an error with error code 20068.<br>- Multiple calls to this API to reduce the scope of permissions will not stack. For example, if the user has granted permissions A and B, the first call to the API reduces the scope to permission A, then `user_access_token` will only contain permission A. If the second call to the API reduces the scope to permission B, then `user_access_token` will only contain permission B.<br>- The effective permission list can be viewed via the `scope` return value of this API.<br>**Format Requirements:** Space-separated list of `scope`<br>**Example Value:** `auth:user.id:read task:task:read`

### Request Body Example
```json
{
    "grant_type": "authorization_code",
    "client_id": "cli_a5ca35a685b0x26e",
    "client_secret": "baBqE5um9LbFGDy3X7LcfxQX1sqpXlwy",
    "code": "a61hb967bd094dge949h79bbexd16dfe",
    "redirect_uri": "https://example.com/api/oauth/callback",
    "code_verifier": "TxYmzM4PHLBlqm5NtnCmwxMH8mFlRWl_ipie3O0aVzo"
}
```

## Response
The response body type is `application/json; charset=utf-8`.

### Response Body
**Notice**：**The length of `access_token` and `refresh_token` in the response body is considerably long**, typically between 1~2KB. However, the length may further increase due to a larger number of `scope` or subsequent changes. It is recommended to reserve 4KB of storage space.

Name | Type | Description
---|---|---
code | int | Error code. A value of 0 indicates a successful request, while non-zero values indicate failure. Please refer to the [Error Codes](#error-codes) section below for proper handling.
access_token | string | Equivalent to `user_access_token`, returned only if the request is successful.
expires_in | int | The validity period of `user_access_token` in seconds, returned only if the request is successful.<br>**Notice**：It is recommended to use this field to determine the expiration time of `user_access_token`, rather than hardcoding the validity period.
refresh_token | string | Used to refresh the `user_access_token`. See [Refresh user_access_token](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/authentication-management/access-token/refresh-user-access-token) for details. This field is returned only if the request is successful and the user grants the `offline_access` permission.<br>**Notice**：If you set the `scope` request parameter during the refresh of `user_access_token` and need to return `refresh_token`, you must include `offline_access` in the `scope` parameter. Moreover, the `refresh_token` can only be used once.
refresh_token_expires_in | int | The validity period of the `refresh_token` in seconds, returned only when `refresh_token` is returned.<br>**Notice**：It is recommended to call the [refresh user_access_token](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/authentication-management/access-token/refresh-user-access-token) interface before expiration to obtain a new `refresh_token`.
token_type | string | The value is fixed at `Bearer`, returned only if the request is successful.
scope | string | List of permissions associated with the `access_token` obtained in this request, separated by spaces, returned only if the request is successful.
error | string | Error type, returned only if the request fails.
error_description | string | Specific error information, returned only if the request fails.

### Response Body Examples

Successful response example:

```json
{
    "code": 0,
    "access_token": "eyJhbGciOiJFUzI1NiIs**********X6wrZHYKDxJkWwhdkrYg",
    "expires_in": 7200, // This is not a fixed value. Please ensure the validity of the access_token by using the actual value returned in the response body.
    "refresh_token": "eyJhbGciOiJFUzI1NiIs**********XXOYOZz1mfgIYHwM8ZJA",
    "refresh_token_expires_in": 604800, // This is not a fixed value. Please ensure the validity of the refresh_token by using the actual value returned in the response body.
    "scope": "auth:user.id:read offline_access task:task:read user_profile",
    "token_type": "Bearer"
}
```

Failed response example:
```json
{
    "code": 20050,
    "error": "server_error",
    "error_description": "An unexpected server error occurred. Please retry your request."
}
```

### Error Codes

HTTP Status Code | Error Code | Description | Troubleshooting Suggestions
---|---|---|---
400 | 20001 | The request is missing a required parameter. | A required parameter is missing. Please check whether the parameters provided in the request are correct.
400 | 20002 | The client secret is invalid. | Application authentication failed. Please verify if the provided `client_id` and `client_secret` are correct.
400 | 20003 | The authorization code is not found. Please note that an authorization code can only be used once. | Invalid authorization code. Please verify if the authorization code is valid, and remember that an authorization code can only be used once.
400 | 20004 | The authorization code has expired. | The authorization code has expired. Please use it within 5 minutes of generation.
400 | 20008 | The user does not exist. | The user does not exist. Please check the current status of the user initiating the authorization.
400 | 20009 | The specified app is not installed. | The application is not installed on the tenant. Please check the application status.
400 | 20010 | The user does not have permission to use this app. | The user does not have permission to use the application. Please verify whether the user initiating the authorization still has permission to use the application.
400 | 20024 | The provided authorization code or refresh token does not match the provided client ID. | The provided authorization code does not match the `client_id`. Please do not mix credentials from different applications.
400 | 20036 | The specified grant_type is not supported. | Invalid `grant_type`. Please ensure that the `grant_type` field in the request body is correct.
400 | 20048 | The specified app does not exist. | The application does not exist. Please check the application status.
400 | 20049 | PKCE code challenge failed. | PKCE verification failed. Please ensure that the `code_verifier` field in the request body exists and is valid.
500 | 20050 | An unexpected server error occurred. Please retry your request. | An internal server error occurred. Please try again later and, if the error persists, contact [technical support](https://applink.feishu.cn/TLJpeNdW).
400 | 20063 | The request is malformed. Please check your request. | The request body is missing necessary fields. Please supplement the fields based on specific error information.
400 | 20065 | The authorization code has been used. Please note that an authorization code can only be used once. | The authorization code has been used. An authorization code can only be used once. Please check for possible reuse.
400 | 20066 | The user status is invalid. | The user status is invalid. Please check the current status of the user initiating the authorization.
400 | 20067 | The provided scope list contains duplicate scopes. Please ensure all scopes are unique. | The `scope` list is invalid as it contains duplicate items. Please ensure that there are no duplicates in the provided `scope` list.
400 | 20068 | The provided scope list contains scopes that are not permitted. Please ensure all scopes are allowed. | Invalid `scope` list; it contains permissions that the user has not authorized. The permissions passed in the `scope` parameter of the current API must be a subset of the `scope` parameter value when [obtaining the authorization code](https://open.feishu.cn/document/common-capabilities/sso/api/obtain-oauth-code).<br>For example, if the user authorized permissions A and B when obtaining the authorization code, the `scope` value passed in the current API can only be permissions A or B. If permission C is passed, the current error code will be returned.
400 | 20069 | The specified app is not enabled. | The application is not enabled. Please check the application status.
400 | 20070 | Multiple authentication methods were provided. Please only use one to proceed. | Both `Basic Authentication` and `client_secret` authentication methods were used in the request. Please use only the `client_id` and `client_secret` authentication methods to call this API.
400 | 20071 | The provided redirect URI does not match the one used during authorization. | The `redirect_uri` is invalid. Please ensure that the `redirect_uri` matches the one provided during the [authorization code request](https://open.feishu.cn/document/common-capabilities/sso/api/obtain-oauth-code).
503 | 20072 | The server is temporarily unavailable. Please retry your request. | The service is temporarily unavailable. Please try again later.

## Code Example
**Notice**：The code examples provided here are **for reference only** and should not be used directly in a production environment.

### Golang

Steps to run the example program below:
1. Click the copy button at the top right of the code block below, copy the code into a local file, and save it as `main.go`.
2. Complete the configuration by referring to the comments section.
3. In the directory where `main.go` is located, create a `.env` file with the following contents:
    ```bash
    APP_ID=cli_xxxxxx # This is only an example value. Please use your application's App ID. Obtain it by: Developer Console -> Basic Info -> Credentials & Basic Info -> Credentials -> App ID
    APP_SECRET=xxxxxx # This is only an example value. Please use your application's App Secret. Obtain it by: Developer Console -> Basic Info -> Credentials & Basic Info -> Credentials -> App Secret
    ```
4. Execute the following commands in the directory where `main.go` is located:
    ```bash
    go mod init oauth-test
    go get github.com/gin-gonic/gin
    go get github.com/gin-contrib/sessions
    go get github.com/gin-contrib/sessions/cookie
    go get github.com/joho/godotenv
    go get golang.org/x/oauth2
    go run main.go
    ```
5. Open a browser and navigate to [http://localhost:8080](http://localhost:8080), and follow the page instructions to complete the authorization flow.
