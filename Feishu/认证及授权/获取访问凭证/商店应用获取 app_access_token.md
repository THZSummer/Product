# Store applications get app_access_token

Store applications get `app_access_token` through this interface.
**Note:** The maximum valid period of the `app_access_token` is 2 hours. If the valid period is less than 30 minutes, calling this interface will return a new `app_access_token`, which will simultaneously exist two valid `app_access_token`.

## Request

Facts | &nbsp;
---|---
HTTP URL | https://open.feishu.cn/open-apis/auth/v3/app_access_token
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
app_id | string | Yes | The unique identifier of the application is obtained after the application is created. For a detailed introduction to `app_id`, please refer to the introduction of [General parameters](https://open.feishu.cn/document/ukTMukTMukTM/uYTM5UjL2ETO14iNxkTN/terminology).<br>**Example value:** "cli_slkdjalasdkjasd"
app_secret | string | Yes | Application key, obtained after creating the application. For a detailed introduction to `app_secret`, please refer to the introduction of [General parameters](https://open.feishu.cn/document/ukTMukTMukTM/uYTM5UjL2ETO14iNxkTN/terminology).<br>**Example value:** "dskLLdkasdjlasdKK"
app_ticket | string | Yes | The platform pushes temporary credentials to the store application at regular intervals. The way to obtain this credential is as follows:<br>1. After creating a store application, you need to configure [event subscription](https://open.feishu.cn/document/ukTMukTMukTM/uUTNz4SN1MjL1UzM) for the application and subscribe to [app_ticket event](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/application-v6/event/app_ticket-events).<br>After subscription, the Feishu Open Platform will automatically push app_ticket to the server receiving the event at a frequency of 1 time/hour.<br>2. (Optional) Call the [Retrieve app_ticket](https://open.feishu.cn/document/ukTMukTMukTM/ukDNz4SO0MjL5QzM/auth-v3/auth/app_ticket_resend) interface to actively trigger the [app_ticket event](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/application-v6/event/app_ticket-events).<br>**Example value:** "dskLLdkasd"

### Request body example

```json
{
    "app_id": "cli_slkdjalasdkjasd",
    "app_secret": "dskLLdkasdjlasdKK",
    "app_ticket": "dskLLdkasd"
}
```

## Response

### Response body

Parameter | Type | Description
---|---|---
code | int | Error code, non-0 indicates failure.
msg | string | Error description.
app_access_token | string | The access token.
expire | int | The expiration time of app_access_token, in seconds.

### Response body example

```json
{
    "code": 0,
    "msg": "success",
    "app_access_token": "a-6U1SbDiM6XIH2DcTCPyeub",
    "expire": 7140
}
```

### Error code

For a detailed description of error codes, see Introduction to [Generic error code](https://open.feishu.cn/document/ukTMukTMukTM/ugjM14COyUjL4ITN).
