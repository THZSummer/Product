# Store applications get tenant_access_token

Store applications get `tenant_access_token` through this interface.
**Note:** The maximum valid period of `tenant_access_token` is 2 hours. If the valid period is less than 30 minutes, calling this interface will return a new `tenant_access_token`, which will have two valid `tenant_access_token` at the same time.

## Request

Facts | &nbsp;
---|---
HTTP URL | https://open.feishu.cn/open-apis/auth/v3/tenant_access_token
HTTP Method | POST
Supported app types | Store App
Required scopes<br>**To use this API, you must have at least 1 of the listed scopes.** | None

### Request header

Parameter | Type | Required | Description
---|---|---|---
Content-Type | string | Yes | **Fixed value**: "application/json; charset=utf-8"

### Request body

Parameter | Type | Required | Description
---|---|---|---
app_access_token | string | Yes | Application access credentials, obtained through the [Store applications get app_access_token](https://open.feishu.cn/document/ukTMukTMukTM/ukDNz4SO0MjL5QzM/auth-v3/auth/app_access_token)<br>**Example value:** "a-32bd8551db2f081cbfd26293f27516390b9feb04"
tenant_key | string | Yes | The unique identity of the tenant on Feishu, which can also be understood as the corporate identity<br>It can be obtained as follows:<br>- When the industry opens an application, the open API is pushed to the application. For details, please refer to [App enabled initially](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/application-v6/event/app-first-enabled)<br>- When the user logs in to the Mini Program, H5 application or browser application, the user's identity information is obtained<br>**Example value** : "73658811060f175d"

### Request body example

```json
{
    "app_access_token": "a-32bd8551db2f081cbfd26293f27516390b9feb04",
    "tenant_key": "73658811060f175d"
}
```

## Response

### Response body

Parameter | Type | Description
---|---|---
code | int | Error code, non-0 indicates failure.
msg | string | Error description.
tenant_access_token | string | The access token.
expire | int | The expiration time of `tenant_access_token`, in seconds.

### Response body example

```json
{
    "code": 0,
    "msg": "success",
    "tenant_access_token": "t-caecc734c2e3328a62489fe0648c4b98779515d3",
    "expire": 7140
}
```

### Error code

For a detailed description of error codes, see Introduction to [Generic error code](https://open.feishu.cn/document/ukTMukTMukTM/ugjM14COyUjL4ITN).
