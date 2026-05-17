CREATE OR REPLACE FUNCTION get_receipts_price_over(
    p_price INT
)
    RETURNS TABLE(
                     receipt_id INT,
                     cassette_id INT,
                     video_id INT,
                     service_id INT,
                     receipt_date DATE,
                     receipt_price INT
                 )
AS $$
BEGIN
    RETURN QUERY
        SELECT
            R.ReceiptID,
            R.CassetteID,
            R.VideoID,
            R.ServiceID,
            R.Date,
            R.Price
        FROM Receipt R
        WHERE R.Price > p_price;
END;
$$ LANGUAGE plpgsql;