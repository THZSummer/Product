# Get JSAPI Authorization Ticket

This api is used to get temporary ticket to invoke js api. With this ticket, the request won't be intercept

## Instructions

- This interface is suitable for web application authentication scenarios. For details, see [Authentication Call JSAPI](https://open.feishu.cn/document/uYjL24iN/uEzM4YjLxMDO24SMzgjN).
- The number of API calls to obtain jsapi_ticket is very limited. Frequently refreshing jsapi_ticket will limit API calls and affect your own business. Therefore, it is recommended that you cache jsapi_ticket globally in your own service.
- After obtaining jsapi_ticket, please combine the returned valid time parameter (expire_in) to set the logic of timing to obtain jsapi_ticket to avoid business exceptions caused by expired credentials.

## Request

Facts | &nbsp;
---|---
HTTP URL | https://open.feishu.cn/open-apis/jssdk/ticket/get
HTTP Method | POST

### Request header

Parameter | Type | Required | Description
---|---|---|---
Authorization | string | Yes | `tenant_access_token`<br>**Value Format**: "Bearer `access_token`"<br>**Example Value**: "Bearer t-7f1bcd13fc57d46bac21793a18e560"<br>[Learn more about obtaining and using access_token.](https://open.feishu.cn/document/ukTMukTMukTM/uMTNz4yM1MjLzUzM)
Content-Type | string | Yes | **Fixed value**: "application/json; charset=utf-8"

## Response

### Response body

| Param         | Type           | Explanation      |
| --------- | ---------------  | -------  |
|code|int|error code|
|msg|string|error description|
|data|object|business data|
|∟ticket|string|temporary ticket to invoke js api|
|∟expire_in|int|valid time for ticket(Unit: second)|

### Response body example

```json
{
    "code": 0,
    "msg": "ok",
    "data": {
        "expire_in": 7200,
        "ticket": "0560604568baf296731aa37f0c8ebe3e049c19d7"
    }
}
```

### Error code

For details, please refer to: [Service-side error codes](https://open.feishu.cn/document/ukTMukTMukTM/ugjM14COyUjL4ITN)
