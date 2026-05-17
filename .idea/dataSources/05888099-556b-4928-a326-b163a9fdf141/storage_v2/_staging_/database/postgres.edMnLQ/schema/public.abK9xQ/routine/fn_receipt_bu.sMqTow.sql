CREATE OR REPLACE FUNCTION fn_receipt_bu()

    RETURNS TRIGGER AS $$

BEGIN

    IF NEW.Price <= 0 THEN

        RAISE EXCEPTION 'Цена должна быть больше 0';

    END IF;

    RETURN NEW;

END;

$$ LANGUAGE plpgsql;

alter function fn_receipt_bu() owner to postgres;

