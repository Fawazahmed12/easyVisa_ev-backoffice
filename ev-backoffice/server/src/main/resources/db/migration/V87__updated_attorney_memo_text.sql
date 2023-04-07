UPDATE
    account_transaction
SET
    memo = REPLACE (
            memo,
            'Paid balance.',
            'Balance Paid'
        );