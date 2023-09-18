import logging


def lambda_handler(event, context):
    """
    Check if sign-up fields are valid
    The function is triggered by Pre sign-up Lambda trigger
    Runtime: Python 3.8
    The configuration of psycopg2 library follows the repo:
    https://github.com/jkehler/awslambda-psycopg2
    """
    logger = logging.getLogger(context.function_name)

    user_id: str = event["userName"]
    email: str = event["request"]["userAttributes"]["email"].strip()
    full_name: str = event["request"]["userAttributes"]["name"].strip()

    logger.info(
        f"User {user_id} tried to sign up with email {email} and name {full_name}"
    )
    return event
