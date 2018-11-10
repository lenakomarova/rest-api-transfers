CREATE TABLE IF NOT EXISTS ACCOUNT_STATE_EVENT (id INTEGER IDENTITY, state varchar(10), inserted date;
CREATE TABLE IF NOT EXISTS MONEY_TRANSFER (acc_id INTEGER, direction varchar(10),
amount number, description varchar(100), involved_acc_id integer, current_balance number, inserted date;
