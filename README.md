#REST API for Money Transfers

| Method | Description |
| --- | --- |
| GET /accounts | Returns all accounts |
| POST /accounts | Creates new account |
| POST /accounts?amount=:amount | For test: Creates new account with **amount** of money |
| GET /accounts/:id | Returns account with **id** |
| DELETE /accounts/:id | Closes account with **id** |
| GET /accounts/:id/transfers | Returns transfers of account with **id** |
| POST /accounts/:id/transfers/:to | Executes transfers from account with **id** to account **to** |

