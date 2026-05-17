create function get_studios_by_year_revenue(p_year integer, p_min_revenue integer)
    returns TABLE(studio_name character varying, total_revenue bigint)
    language plpgsql
as
$$
BEGIN

    RETURN QUERY

        SELECT
            S.StudioName,
            COALESCE(SUM(R.Price),0)

        FROM Studio S

                 LEFT JOIN Film F
                           ON S.StudioID = F.StudioID

                 LEFT JOIN Cassette C
                           ON F.FilmID = C.FilmID

                 LEFT JOIN Receipt R
                           ON C.CasseteID = R.CassetteID

        WHERE F.Year = p_year

        GROUP BY S.StudioID, S.StudioName

        HAVING COALESCE(SUM(R.Price),0)
                   >= p_min_revenue;

END;
$$;

alter function get_studios_by_year_revenue(integer, integer) owner to postgres;

