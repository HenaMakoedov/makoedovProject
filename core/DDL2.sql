SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';
CREATE DATABASE `sakila`;
USE sakila;

CREATE TABLE IF NOT EXISTS actor (
`actor_id` smallint(5) unsigned NOT NULL   AUTO_INCREMENT,
`first_name` varchar(45) NOT NULL   ,
`last_name` varchar(45) NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`actor_id`),
KEY `idx_actor_last_name` (last_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS country (
`country_id` smallint(5) unsigned NOT NULL   AUTO_INCREMENT,
`country` varchar(50) NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`country_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS category (
`category_id` tinyint(3) unsigned NOT NULL   AUTO_INCREMENT,
`name` varchar(25) NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`category_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS city (
`city_id` smallint(5) unsigned NOT NULL   AUTO_INCREMENT,
`city` varchar(50) NOT NULL   ,
`country_id` smallint(5) unsigned NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`city_id`),
KEY `idx_fk_country_id` (country_id),
CONSTRAINT `fk_city_country` FOREIGN KEY(`country_id`) REFERENCES `country` (`country_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS address (
`address_id` smallint(5) unsigned NOT NULL   AUTO_INCREMENT,
`address` varchar(50) NOT NULL   ,
`address2` varchar(50) DEFAULT NULL   ,
`district` varchar(20) NOT NULL   ,
`city_id` smallint(5) unsigned NOT NULL   ,
`postal_code` varchar(10) DEFAULT NULL   ,
`phone` varchar(20) NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`address_id`),
KEY `idx_fk_city_id` (city_id),
CONSTRAINT `fk_address_city` FOREIGN KEY(`city_id`) REFERENCES `city` (`city_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS store (
`store_id` tinyint(3) unsigned NOT NULL   AUTO_INCREMENT,
`manager_staff_id` tinyint(3) unsigned NOT NULL   ,
`address_id` smallint(5) unsigned NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`store_id`),
UNIQUE KEY `idx_unique_manager` (manager_staff_id),
KEY `idx_fk_address_id` (address_id),
CONSTRAINT `fk_store_staff` FOREIGN KEY(`manager_staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_store_address` FOREIGN KEY(`address_id`) REFERENCES `address` (`address_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS language (
`language_id` tinyint(3) unsigned NOT NULL   AUTO_INCREMENT,
`name` char(20) NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`language_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS film (
`film_id` smallint(5) unsigned NOT NULL   AUTO_INCREMENT,
`title` varchar(255) NOT NULL   ,
`description` text DEFAULT NULL   ,
`release_year` year(4) DEFAULT NULL   ,
`language_id` tinyint(3) unsigned NOT NULL   ,
`original_language_id` tinyint(3) unsigned DEFAULT NULL   ,
`rental_duration` tinyint(3) unsigned NOT NULL  DEFAULT 3 ,
`rental_rate` decimal(4,2) NOT NULL  DEFAULT 4.99 ,
`length` smallint(5) unsigned DEFAULT NULL   ,
`replacement_cost` decimal(5,2) NOT NULL  DEFAULT 19.99 ,
`rating` enum('G','PG','PG-13','R','NC-17') DEFAULT NULL  DEFAULT 'G' ,
`special_features` set('Trailers','Commentaries','Deleted Scenes','Behind the Scenes') DEFAULT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`film_id`),
KEY `idx_title` (title),
KEY `idx_fk_language_id` (language_id),
KEY `idx_fk_original_language_id` (original_language_id),
CONSTRAINT `fk_film_language` FOREIGN KEY(`language_id`) REFERENCES `language` (`language_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_film_language_original` FOREIGN KEY(`original_language_id`) REFERENCES `language` (`language_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` TRIGGER `ins_film` AFTER INSERT ON `film`
FOR EACH ROW BEGIN
    INSERT INTO film_text (film_id, title, description)
        VALUES (new.film_id, new.title, new.description);
  END $$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` TRIGGER `upd_film` AFTER UPDATE ON `film`
FOR EACH ROW BEGIN
    IF (old.title != new.title) OR (old.description != new.description) OR (old.film_id != new.film_id)
    THEN
        UPDATE film_text
            SET title=new.title,
                description=new.description,
                film_id=new.film_id
        WHERE film_id=old.film_id;
    END IF;
  END $$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` TRIGGER `del_film` AFTER DELETE ON `film`
FOR EACH ROW BEGIN
    DELETE FROM film_text WHERE film_id = old.film_id;
  END $$
DELIMITER ;

CREATE TABLE IF NOT EXISTS film_category (
`film_id` smallint(5) unsigned NOT NULL   ,
`category_id` tinyint(3) unsigned NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`film_id`,`category_id`),
KEY `fk_film_category_category` (category_id),
CONSTRAINT `fk_film_category_film` FOREIGN KEY(`film_id`) REFERENCES `film` (`film_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_film_category_category` FOREIGN KEY(`category_id`) REFERENCES `category` (`category_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS film_text (
`film_id` smallint(6) NOT NULL   ,
`title` varchar(255) NOT NULL   ,
`description` text DEFAULT NULL   ,
PRIMARY KEY (`film_id`),
FULLTEXT KEY `idx_title_description` (title,description)
)ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS inventory (
`inventory_id` mediumint(8) unsigned NOT NULL   AUTO_INCREMENT,
`film_id` smallint(5) unsigned NOT NULL   ,
`store_id` tinyint(3) unsigned NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`inventory_id`),
KEY `idx_fk_film_id` (film_id),
KEY `idx_store_id_film_id` (store_id,film_id),
CONSTRAINT `fk_inventory_store` FOREIGN KEY(`store_id`) REFERENCES `store` (`store_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_inventory_film` FOREIGN KEY(`film_id`) REFERENCES `film` (`film_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS film_actor (
`actor_id` smallint(5) unsigned NOT NULL   ,
`film_id` smallint(5) unsigned NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`actor_id`,`film_id`),
KEY `idx_fk_film_id` (film_id),
CONSTRAINT `fk_film_actor_actor` FOREIGN KEY(`actor_id`) REFERENCES `actor` (`actor_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_film_actor_film` FOREIGN KEY(`film_id`) REFERENCES `film` (`film_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS staff (
`staff_id` tinyint(3) unsigned NOT NULL   AUTO_INCREMENT,
`first_name` varchar(45) NOT NULL   ,
`last_name` varchar(45) NOT NULL   ,
`address_id` smallint(5) unsigned NOT NULL   ,
`picture` blob DEFAULT NULL   ,
`email` varchar(50) DEFAULT NULL   ,
`store_id` tinyint(3) unsigned NOT NULL   ,
`active` tinyint(1) NOT NULL  DEFAULT 1 ,
`username` varchar(16) NOT NULL   ,
`password` varchar(40) DEFAULT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`staff_id`),
KEY `idx_fk_store_id` (store_id),
KEY `idx_fk_address_id` (address_id),
CONSTRAINT `fk_staff_store` FOREIGN KEY(`store_id`) REFERENCES `store` (`store_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_staff_address` FOREIGN KEY(`address_id`) REFERENCES `address` (`address_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS customer (
`customer_id` smallint(5) unsigned NOT NULL   AUTO_INCREMENT,
`store_id` tinyint(3) unsigned NOT NULL   ,
`first_name` varchar(45) NOT NULL   ,
`last_name` varchar(45) NOT NULL   ,
`email` varchar(50) DEFAULT NULL   ,
`address_id` smallint(5) unsigned NOT NULL   ,
`active` tinyint(1) NOT NULL  DEFAULT 1 ,
`create_date` datetime NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`customer_id`),
KEY `idx_fk_store_id` (store_id),
KEY `idx_fk_address_id` (address_id),
KEY `idx_last_name` (last_name),
CONSTRAINT `fk_customer_address` FOREIGN KEY(`address_id`) REFERENCES `address` (`address_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_customer_store` FOREIGN KEY(`store_id`) REFERENCES `store` (`store_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS rental (
`rental_id` int(11) NOT NULL   AUTO_INCREMENT,
`rental_date` datetime NOT NULL   ,
`inventory_id` mediumint(8) unsigned NOT NULL   ,
`customer_id` smallint(5) unsigned NOT NULL   ,
`return_date` datetime DEFAULT NULL   ,
`staff_id` tinyint(3) unsigned NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`rental_id`),
UNIQUE KEY `rental_date` (rental_date,inventory_id,customer_id),
KEY `idx_fk_inventory_id` (inventory_id),
KEY `idx_fk_customer_id` (customer_id),
KEY `idx_fk_staff_id` (staff_id),
CONSTRAINT `fk_rental_staff` FOREIGN KEY(`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_rental_inventory` FOREIGN KEY(`inventory_id`) REFERENCES `inventory` (`inventory_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_rental_customer` FOREIGN KEY(`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS payment (
`payment_id` smallint(5) unsigned NOT NULL   AUTO_INCREMENT,
`customer_id` smallint(5) unsigned NOT NULL   ,
`staff_id` tinyint(3) unsigned NOT NULL   ,
`rental_id` int(11) DEFAULT NULL   ,
`amount` decimal(5,2) NOT NULL   ,
`payment_date` datetime NOT NULL   ,
`last_update` timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`payment_id`),
KEY `idx_fk_staff_id` (staff_id),
KEY `idx_fk_customer_id` (customer_id),
KEY `fk_payment_rental` (rental_id),
CONSTRAINT `fk_payment_rental` FOREIGN KEY(`rental_id`) REFERENCES `rental` (`rental_id`) ON DELETE SET NULL ON UPDATE CASCADE,
CONSTRAINT `fk_payment_customer` FOREIGN KEY(`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT `fk_payment_staff` FOREIGN KEY(`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE
ALGORITHM = UNDEFINED
DEFINER = `root`@`localhost`
SQL SECURITY INVOKER
VIEW `actor_info` AS
select `a`.`actor_id` AS `actor_id`,`a`.`first_name` AS `first_name`,`a`.`last_name` AS `last_name`,group_concat(distinct concat(`c`.`name`,': ',(select group_concat(`f`.`title` order by `f`.`title` ASC separator ',
 ') from ((`sakila`.`film` `f` join `sakila`.`film_category` `fc` on((`f`.`film_id` = `fc`.`film_id`))) join `sakila`.`film_actor` `fa` on((`f`.`film_id` = `fa`.`film_id`))) where ((`fc`.`category_id` = `c`.`category_id`) and (`fa`.`actor_id` = `a`.`actor_id`)))) order by `c`.`name` ASC separator '; ') AS `film_info` from (((`sakila`.`actor` `a` left join `sakila`.`film_actor` `fa` on((`a`.`actor_id` = `fa`.`actor_id`))) left join `sakila`.`film_category` `fc` on((`fa`.`film_id` = `fc`.`film_id`))) left join `sakila`.`category` `c` on((`fc`.`category_id` = `c`.`category_id`))) group by `a`.`actor_id`,`a`.`first_name`,`a`.`last_name`;

CREATE
ALGORITHM = UNDEFINED
DEFINER = `root`@`localhost`
SQL SECURITY DEFINER
VIEW `customer_list` AS
select `cu`.`customer_id` AS `ID`,concat(`cu`.`first_name`,' ',`cu`.`last_name`) AS `name`,`a`.`address` AS `address`,`a`.`postal_code` AS `zip code`,`a`.`phone` AS `phone`,`sakila`.`city`.`city` AS `city`,`sakila`.`country`.`country` AS `country`,if(`cu`.`active`,'active','') AS `notes`,`cu`.`store_id` AS `SID` from (((`sakila`.`customer` `cu` join `sakila`.`address` `a` on((`cu`.`address_id` = `a`.`address_id`))) join `sakila`.`city` on((`a`.`city_id` = `sakila`.`city`.`city_id`))) join `sakila`.`country` on((`sakila`.`city`.`country_id` = `sakila`.`country`.`country_id`)));

CREATE
ALGORITHM = UNDEFINED
DEFINER = `root`@`localhost`
SQL SECURITY DEFINER
VIEW `film_list` AS
select `sakila`.`film`.`film_id` AS `FID`,`sakila`.`film`.`title` AS `title`,`sakila`.`film`.`description` AS `description`,`sakila`.`category`.`name` AS `category`,`sakila`.`film`.`rental_rate` AS `price`,`sakila`.`film`.`length` AS `length`,`sakila`.`film`.`rating` AS `rating`,group_concat(concat(`sakila`.`actor`.`first_name`,' ',`sakila`.`actor`.`last_name`) separator ',
 ') AS `actors` from ((((`sakila`.`category` left join `sakila`.`film_category` on((`sakila`.`category`.`category_id` = `sakila`.`film_category`.`category_id`))) left join `sakila`.`film` on((`sakila`.`film_category`.`film_id` = `sakila`.`film`.`film_id`))) join `sakila`.`film_actor` on((`sakila`.`film`.`film_id` = `sakila`.`film_actor`.`film_id`))) join `sakila`.`actor` on((`sakila`.`film_actor`.`actor_id` = `sakila`.`actor`.`actor_id`))) group by `sakila`.`film`.`film_id`,`sakila`.`category`.`name`;

CREATE
ALGORITHM = UNDEFINED
DEFINER = `root`@`localhost`
SQL SECURITY DEFINER
VIEW `nicer_but_slower_film_list` AS
select `sakila`.`film`.`film_id` AS `FID`,`sakila`.`film`.`title` AS `title`,`sakila`.`film`.`description` AS `description`,`sakila`.`category`.`name` AS `category`,`sakila`.`film`.`rental_rate` AS `price`,`sakila`.`film`.`length` AS `length`,`sakila`.`film`.`rating` AS `rating`,group_concat(concat(concat(ucase(substr(`sakila`.`actor`.`first_name`,1,1)),lcase(substr(`sakila`.`actor`.`first_name`,2,length(`sakila`.`actor`.`first_name`))),' ',concat(ucase(substr(`sakila`.`actor`.`last_name`,1,1)),lcase(substr(`sakila`.`actor`.`last_name`,2,length(`sakila`.`actor`.`last_name`)))))) separator ',
 ') AS `actors` from ((((`sakila`.`category` left join `sakila`.`film_category` on((`sakila`.`category`.`category_id` = `sakila`.`film_category`.`category_id`))) left join `sakila`.`film` on((`sakila`.`film_category`.`film_id` = `sakila`.`film`.`film_id`))) join `sakila`.`film_actor` on((`sakila`.`film`.`film_id` = `sakila`.`film_actor`.`film_id`))) join `sakila`.`actor` on((`sakila`.`film_actor`.`actor_id` = `sakila`.`actor`.`actor_id`))) group by `sakila`.`film`.`film_id`,`sakila`.`category`.`name`;

CREATE
ALGORITHM = UNDEFINED
DEFINER = `root`@`localhost`
SQL SECURITY DEFINER
VIEW `sales_by_film_category` AS
select `c`.`name` AS `category`,sum(`p`.`amount`) AS `total_sales` from (((((`sakila`.`payment` `p` join `sakila`.`rental` `r` on((`p`.`rental_id` = `r`.`rental_id`))) join `sakila`.`inventory` `i` on((`r`.`inventory_id` = `i`.`inventory_id`))) join `sakila`.`film` `f` on((`i`.`film_id` = `f`.`film_id`))) join `sakila`.`film_category` `fc` on((`f`.`film_id` = `fc`.`film_id`))) join `sakila`.`category` `c` on((`fc`.`category_id` = `c`.`category_id`))) group by `c`.`name` order by sum(`p`.`amount`) desc;

CREATE
ALGORITHM = UNDEFINED
DEFINER = `root`@`localhost`
SQL SECURITY DEFINER
VIEW `sales_by_store` AS
select concat(`c`.`city`,',\n',`cy`.`country`) AS `store`,concat(`m`.`first_name`,' ',`m`.`last_name`) AS `manager`,sum(`p`.`amount`) AS `total_sales` from (((((((`sakila`.`payment` `p` join `sakila`.`rental` `r` on((`p`.`rental_id` = `r`.`rental_id`))) join `sakila`.`inventory` `i` on((`r`.`inventory_id` = `i`.`inventory_id`))) join `sakila`.`store` `s` on((`i`.`store_id` = `s`.`store_id`))) join `sakila`.`address` `a` on((`s`.`address_id` = `a`.`address_id`))) join `sakila`.`city` `c` on((`a`.`city_id` = `c`.`city_id`))) join `sakila`.`country` `cy` on((`c`.`country_id` = `cy`.`country_id`))) join `sakila`.`staff` `m` on((`s`.`manager_staff_id` = `m`.`staff_id`))) group by `s`.`store_id` order by `cy`.`country`,`c`.`city`;

CREATE
ALGORITHM = UNDEFINED
DEFINER = `root`@`localhost`
SQL SECURITY DEFINER
VIEW `staff_list` AS
select `s`.`staff_id` AS `ID`,concat(`s`.`first_name`,' ',`s`.`last_name`) AS `name`,`a`.`address` AS `address`,`a`.`postal_code` AS `zip code`,`a`.`phone` AS `phone`,`sakila`.`city`.`city` AS `city`,`sakila`.`country`.`country` AS `country`,`s`.`store_id` AS `SID` from (((`sakila`.`staff` `s` join `sakila`.`address` `a` on((`s`.`address_id` = `a`.`address_id`))) join `sakila`.`city` on((`a`.`city_id` = `sakila`.`city`.`city_id`))) join `sakila`.`country` on((`sakila`.`city`.`country_id` = `sakila`.`country`.`country_id`)));

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `film_in_stock` (IN p_film_id int(11),IN p_store_id int(11),OUT p_film_count int(11))
READS SQL DATA

BEGIN
     SELECT inventory_id
     FROM inventory
     WHERE film_id = p_film_id
     AND store_id = p_store_id
     AND inventory_in_stock(inventory_id);

     SELECT FOUND_ROWS() INTO p_film_count;
END $$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `film_not_in_stock` (IN p_film_id int(11),IN p_store_id int(11),OUT p_film_count int(11))
READS SQL DATA

BEGIN
     SELECT inventory_id
     FROM inventory
     WHERE film_id = p_film_id
     AND store_id = p_store_id
     AND NOT inventory_in_stock(inventory_id);

     SELECT FOUND_ROWS() INTO p_film_count;
END $$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `rewards_report` (IN min_monthly_purchases tinyint(3) unsigned,IN min_dollar_amount_purchased decimal(10,2) unsigned,OUT count_rewardees int(11))
READS SQL DATA

proc: BEGIN

    DECLARE last_month_start DATE;
    DECLARE last_month_end DATE;

    /* Some sanity checks... */
    IF min_monthly_purchases = 0 THEN
        SELECT 'Minimum monthly purchases parameter must be > 0';
        LEAVE proc;
    END IF;
    IF min_dollar_amount_purchased = 0.00 THEN
        SELECT 'Minimum monthly dollar amount purchased parameter must be > $0.00';
        LEAVE proc;
    END IF;

    /* Determine start and end time periods */
    SET last_month_start = DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH);
    SET last_month_start = STR_TO_DATE(CONCAT(YEAR(last_month_start),'-',MONTH(last_month_start),'-01'),'%Y-%m-%d');
    SET last_month_end = LAST_DAY(last_month_start);

    /*
        Create a temporary storage area for
        Customer IDs.
    */
    CREATE TEMPORARY TABLE tmpCustomer (customer_id SMALLINT UNSIGNED NOT NULL PRIMARY KEY);

    /*
        Find all customers meeting the
        monthly purchase requirements
    */
    INSERT INTO tmpCustomer (customer_id)
    SELECT p.customer_id
    FROM payment AS p
    WHERE DATE(p.payment_date) BETWEEN last_month_start AND last_month_end
    GROUP BY customer_id
    HAVING SUM(p.amount) > min_dollar_amount_purchased
    AND COUNT(customer_id) > min_monthly_purchases;

    /* Populate OUT parameter with count of found customers */
    SELECT COUNT(*) FROM tmpCustomer INTO count_rewardees;

    /*
        Output ALL customer information of matching rewardees.
        Customize output as needed.
    */
    SELECT c.*
    FROM tmpCustomer AS t
    INNER JOIN customer AS c ON t.customer_id = c.customer_id;

    /* Clean up */
    DROP TABLE tmpCustomer;
END $$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `get_customer_balance` (p_customer_id int(11),p_effective_date datetime)RETURNS decimal(5,2)
READS SQL DATA
DETERMINISTIC

BEGIN

       #OK, WE NEED TO CALCULATE THE CURRENT BALANCE GIVEN A CUSTOMER_ID AND A DATE
       #THAT WE WANT THE BALANCE TO BE EFFECTIVE FOR. THE BALANCE IS:
       #   1) RENTAL FEES FOR ALL PREVIOUS RENTALS
       #   2) ONE DOLLAR FOR EVERY DAY THE PREVIOUS RENTALS ARE OVERDUE
       #   3) IF A FILM IS MORE THAN RENTAL_DURATION * 2 OVERDUE, CHARGE THE REPLACEMENT_COST
       #   4) SUBTRACT ALL PAYMENTS MADE BEFORE THE DATE SPECIFIED

  DECLARE v_rentfees DECIMAL(5,2); #FEES PAID TO RENT THE VIDEOS INITIALLY
  DECLARE v_overfees INTEGER;      #LATE FEES FOR PRIOR RENTALS
  DECLARE v_payments DECIMAL(5,2); #SUM OF PAYMENTS MADE PREVIOUSLY

  SELECT IFNULL(SUM(film.rental_rate),0) INTO v_rentfees
    FROM film, inventory, rental
    WHERE film.film_id = inventory.film_id
      AND inventory.inventory_id = rental.inventory_id
      AND rental.rental_date <= p_effective_date
      AND rental.customer_id = p_customer_id;

  SELECT IFNULL(SUM(IF((TO_DAYS(rental.return_date) - TO_DAYS(rental.rental_date)) > film.rental_duration,
        ((TO_DAYS(rental.return_date) - TO_DAYS(rental.rental_date)) - film.rental_duration),0)),0) INTO v_overfees
    FROM rental, inventory, film
    WHERE film.film_id = inventory.film_id
      AND inventory.inventory_id = rental.inventory_id
      AND rental.rental_date <= p_effective_date
      AND rental.customer_id = p_customer_id;


  SELECT IFNULL(SUM(payment.amount),0) INTO v_payments
    FROM payment

    WHERE payment.payment_date <= p_effective_date
    AND payment.customer_id = p_customer_id;

  RETURN v_rentfees + v_overfees - v_payments;
END $$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `inventory_held_by_customer` (p_inventory_id int(11))RETURNS int(11)
READS SQL DATA

BEGIN
  DECLARE v_customer_id INT;
  DECLARE EXIT HANDLER FOR NOT FOUND RETURN NULL;

  SELECT customer_id INTO v_customer_id
  FROM rental
  WHERE return_date IS NULL
  AND inventory_id = p_inventory_id;

  RETURN v_customer_id;
END $$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `inventory_in_stock` (p_inventory_id int(11))RETURNS tinyint(1)
READS SQL DATA

BEGIN
    DECLARE v_rentals INT;
    DECLARE v_out     INT;

    #AN ITEM IS IN-STOCK IF THERE ARE EITHER NO ROWS IN THE rental TABLE
    #FOR THE ITEM OR ALL ROWS HAVE return_date POPULATED

    SELECT COUNT(*) INTO v_rentals
    FROM rental
    WHERE inventory_id = p_inventory_id;

    IF v_rentals = 0 THEN
      RETURN TRUE;
    END IF;

    SELECT COUNT(rental_id) INTO v_out
    FROM inventory LEFT JOIN rental USING(inventory_id)
    WHERE inventory.inventory_id = p_inventory_id
    AND rental.return_date IS NULL;

    IF v_out > 0 THEN
      RETURN FALSE;
    ELSE
      RETURN TRUE;
    END IF;
END $$
DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
