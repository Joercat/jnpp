from flask import Flask, request, jsonify
from flask_cors import CORS
import datetime

app = Flask(__name__)
CORS(app)

@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        'status': 'OK',
        'server': 'Python',
        'timestamp': datetime.datetime.now().isoformat()
    })

@app.route('/analyze', methods=['POST'])
def analyze_text():
    data = request.get_json()
    
    if not data or 'text' not in data:
        return jsonify({'error': 'No text provided'}), 400
    
    text = data['text']
    
    # Perform text analysis
    words = text.split()
    word_count = len(words)
    char_count = len(text)
    unique_words = len(set(word.lower() for word in words))
    reversed_text = text[::-1]
    uppercase_text = text.upper()
    
    return jsonify({
        'text': text,
        'word_count': word_count,
        'char_count': char_count,
        'unique_words': unique_words,
        'reversed': reversed_text,
        'uppercase': uppercase_text,
        'server': 'Python',
        'timestamp': datetime.datetime.now().isoformat()
    })

@app.route('/', methods=['GET'])
def root():
    return jsonify({
        'message': 'Python Server is running!',
        'endpoints': ['/health', '/analyze'],
        'server': 'Python'
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8002, debug=False)
