from flask import Flask, render_template, request
import datetime
import requests

JAVA_REST_API_URL = "http://localhost:8080/restful_api_war_exploded/reasoner?query="

app = Flask(__name__)
app.config['DEBUG'] = True

session_queries = list()

def get_response(query):
    response = requests.get(JAVA_REST_API_URL + query).text
    return response

def get_current_time():
    return datetime.datetime.now().strftime("%A, %d. %B %Y %I:%M%p")

@app.route('/', methods=['GET', 'POST'])
def index():
    query = request.form.get('user_msg')
    if query:
        session_queries.append(('user', query, get_current_time()))
        response = get_response(query)
        session_queries.append(('bot', response, get_current_time()))
    return render_template('chat.html', queries=session_queries)

if __name__ == '__main__':
    app.run()
