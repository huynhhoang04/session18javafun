
CREATE TABLE IF NOT EXISTS product (
    product_id SERIAL PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL UNIQUE,
    product_price FLOAT NOT NULL CHECK (product_price > 0),
    product_title VARCHAR(200) NOT NULL,
    product_created DATE NOT NULL,
    product_catalog VARCHAR(100) NOT NULL,
    product_status INT DEFAULT 1 CHECK (product_status IN (0, 1))
);

CREATE OR REPLACE PROCEDURE add_product(
    p_name VARCHAR, 
    p_price FLOAT, 
    p_title VARCHAR, 
    p_created DATE, 
    p_catalog VARCHAR, 
    p_status INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO product (product_name, product_price, product_title, product_created, product_catalog, product_status) 
    VALUES (p_name, p_price, p_title, p_created, p_catalog, p_status);
END;
$$;

CREATE OR REPLACE PROCEDURE update_product(
    p_id INT,
    p_name VARCHAR, 
    p_price FLOAT, 
    p_title VARCHAR, 
    p_catalog VARCHAR, 
    p_status INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE product 
    SET product_name = p_name, 
        product_price = p_price, 
        product_title = p_title, 
        product_catalog = p_catalog, 
        product_status = p_status
    WHERE product_id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_product(p_id INT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM product WHERE product_id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION get_all_products()
RETURNS TABLE (
    out_id INT, out_name VARCHAR, out_price FLOAT, out_title VARCHAR, out_created DATE, out_catalog VARCHAR, out_status INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT product_id, product_name, product_price, product_title, product_created, product_catalog, product_status FROM product ORDER BY product_id;
END;
$$;

CREATE OR REPLACE FUNCTION get_product_by_id(p_id INT)
RETURNS TABLE (
    out_id INT, out_name VARCHAR, out_price FLOAT, out_title VARCHAR, out_created DATE, out_catalog VARCHAR, out_status INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT product_id, product_name, product_price, product_title, product_created, product_catalog, product_status FROM product WHERE product_id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION search_product_by_name(p_keyword VARCHAR)
RETURNS TABLE (
    out_id INT, out_name VARCHAR, out_price FLOAT, out_title VARCHAR, out_created DATE, out_catalog VARCHAR, out_status INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT product_id, product_name, product_price, product_title, product_created, product_catalog, product_status 
    FROM product 
    WHERE product_name ILIKE '%' || p_keyword || '%';
END;
$$;

CREATE OR REPLACE FUNCTION check_catalog_exists(p_catalog VARCHAR)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE
    v_count INT;
BEGIN
    SELECT COUNT(*) INTO v_count FROM product WHERE product_catalog = p_catalog;
    RETURN v_count > 0;
END;
$$;

CREATE OR REPLACE FUNCTION stat_product_by_catalog()
RETURNS TABLE (
    out_catalog VARCHAR,
    out_count BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT product_catalog, COUNT(*) FROM product GROUP BY product_catalog;
END;
$$;

CREATE OR REPLACE FUNCTION sort_products_by_price_asc()
RETURNS TABLE (
    out_id INT, out_name VARCHAR, out_price FLOAT, out_title VARCHAR, out_created DATE, out_catalog VARCHAR, out_status INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT product_id, product_name, product_price, product_title, product_created, product_catalog, product_status FROM product ORDER BY product_price ASC;
END;
$$;
