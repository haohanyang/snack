import os
import logging
import psycopg2

def lambda_handler(event, context):
    """
    Create a new user in the PostgreSQL database
    The function is triggered by AWS Cognito
    Runtime: Python 3.8
    The configuration of psycopg2 library follows the repo:
    https://github.com/jkehler/awslambda-psycopg2
    """
    logger = logging.getLogger(context.function_name)
    user_id: str = event["userName"]
    email: str = event["request"]["userAttributes"]["email"].strip()
    username: str = event["request"]["userAttributes"]["preferred_username"].strip()
    full_name: str = event["request"]["userAttributes"]["name"].strip()

    # Username should be between 4 and 30 characters
    # And only contain alphanumeric characters, underscores,dots and dashes
    if len(username) < 4 or len(username) > 30:
        raise Exception("Username should be between 4 and 30 characters")
    
    for char in username:
        if not char.isalnum() and char not in ["_", ".", "-"]:
            raise Exception("Username should only contain alphanumeric characters, underscores, dots and dashes")

    
    # Email should be at most 50 characters long
    if len(email) > 50:
        raise Exception("Email should be at most 50 characters long")
    
    # Full name should be at most 50 characters long
    if len(full_name) > 50:
        raise Exception("Full name should be at most 50 characters long")
    
    # Connect to the PostgreSQL database
    conn = psycopg2.connect(dbname=os.environ["DB_NAME"], user=os.environ["DB_USER"], password=os.environ["DB_PASSWORD"], host=os.environ["DB_HOST"])

    default_avatar = "https://unpkg.com/ionicons@7.1.0/dist/svg/person-circle-outline.svg"
    # Add the user to the database
    with conn.cursor() as cur:
        cur.execute("INSERT INTO app.users (id, username, email, full_name, avatar, background_image, bio) VALUES (%s, %s, %s, %s, %s, %s, %s)", 
                (user_id, username, email, full_name, default_avatar, "", ""))
        conn.commit()
    
    logger.info("User " + user_id + " created")
    return event


