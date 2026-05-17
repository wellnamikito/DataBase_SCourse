CREATE OR REPLACE FUNCTION get_studios_by_year_revenue(
    p_year INT,
    p_min_revenue INT
)
    RETURNS TABLE(
                     studio_name VARCHAR(100),
                     total_revenue BIGINT
                 )
AS $$
BEGIN
    RETURN QUERY
        SELECT
            S.StudioName,
            COALESCE(SUM(R.Price),0)::BIGINT
        FROM Studio S
                 LEFT JOIN Film F ON S.StudioID = F.StudioID
                 LEFT JOIN Cassette C ON F.FilmID = C.FilmID
                 LEFT JOIN Receipt R ON C.CasseteID = R.CassetteID
        WHERE F.Year = p_year::TEXT
        GROUP BY S.StudioID, S.StudioName
        HAVING COALESCE(SUM(R.Price),0) >= p_min_revenue;
END;
$$ LANGUAGE plpgsql;