CREATE OR REPLACE FUNCTION get_video_revenue_over(
    p_min_revenue INT
)
    RETURNS TABLE(
                     video_caption VARCHAR(100),
                     total_revenue BIGINT
                 )
AS $$
BEGIN
    RETURN QUERY
        SELECT
            V.Caption,
            COALESCE(SUM(R.Price),0)::BIGINT
        FROM Video V
                 LEFT JOIN Receipt R ON V.VideoID = R.VideoID
        GROUP BY V.VideoID, V.Caption
        HAVING COALESCE(SUM(R.Price),0) > p_min_revenue;
END;
$$ LANGUAGE plpgsql;

