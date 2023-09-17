import os
import logging
import psycopg2
from psycopg2 import sql


def lambda_handler(event, context):
    """
    Create a new user in the PostgreSQL database
    The function is triggered by Post confirmation Lambda trigger
    Runtime: Python 3.8
    The configuration of psycopg2 library follows the repo:
    https://github.com/jkehler/awslambda-psycopg2
    """
    logger = logging.getLogger(context.function_name)
    logger.info(event)
    logger.info(context)

    user_id: str = event["userName"]
    email: str = event["request"]["userAttributes"]["email"].strip()
    full_name: str = event["request"]["userAttributes"]["name"].strip()

    # Connect to the PostgreSQL database
    conn = psycopg2.connect(
        dbname=os.environ["DB_NAME"],
        user=os.environ["DB_USERNAME"],
        password=os.environ["DB_PASSWORD"],
        host=os.environ["DB_HOST"],
    )

    default_avatar = (
        "https://unpkg.com/ionicons@7.1.0/dist/svg/person-circle-outline.svg"
    )

    schema_name = "app2"
    table_name = "users"
    # Add the user to the database
    with conn.cursor() as cur:
        cur.execute(
            sql.SQL(
                "INSERT INTO {} (id, email, full_name, avatar, background_image, bio) VALUES (%s, %s, %s, %s, %s, %s)"
            ).format(sql.Identifier(schema_name, table_name)),
            [user_id, email, full_name, default_avatar, "", ""],
        )
        conn.commit()
    logger.info(
        f"User {user_id} was created in the database with email {email} and name {full_name}"
    )
    return event
