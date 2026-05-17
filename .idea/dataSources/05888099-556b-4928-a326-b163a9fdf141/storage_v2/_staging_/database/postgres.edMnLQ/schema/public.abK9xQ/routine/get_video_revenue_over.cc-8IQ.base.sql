create function get_video_revenue_over(p_min_revenue integer)
    returns TABLE(video_caption character varying, total_revenue bigint)
    language plpgsql
as
$$
BEGIN

    RETURN QUERY

        SELECT
            V.Caption,
            SUM(R.Price)

        FROM Video V

                 INNER JOIN Receipt R
                            ON V.VideoID = R.VideoID

        GROUP BY V.VideoID, V.Caption

        HAVING SUM(R.Price) > p_min_revenue;

END;
$$;

alter function get_video_revenue_over(integer) owner to postgres;

