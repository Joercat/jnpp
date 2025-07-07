<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Get the request URI and remove query parameters
$requestUri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$requestUri = rtrim($requestUri, '/');

// Route handling
switch ($requestUri) {
    case '/health':
    case '/health.php':
        handleHealth();
        break;
    
    case '/time':
    case '/time.php':
        handleTime();
        break;
    
    case '':
    case '/':
    default:
        handleRoot();
        break;
}

function handleHealth() {
    $response = [
        'status' => 'OK',
        'server' => 'PHP',
        'timestamp' => date('c'),
        'php_version' => phpversion()
    ];
    
    echo json_encode($response);
}

function handleTime() {
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $input = json_decode(file_get_contents('php://input'), true);
        $name = isset($input['name']) ? $input['name'] : 'World';
    } else {
        $name = isset($_GET['name']) ? $_GET['name'] : 'World';
    }
    
    // Simple string operations instead of timezone handling
    $currentTime = date('Y-m-d H:i:s');
    $greeting = "Hello, " . ucfirst($name) . "!";
    $reversed = strrev($name);
    $uppercase = strtoupper($name);
    $length = strlen($name);
    
    $response = [
        'name' => $name,
        'greeting' => $greeting,
        'reversed' => $reversed,
        'uppercase' => $uppercase,
        'length' => $length,
        'server_time' => $currentTime,
        'server' => 'PHP',
        'timestamp' => date('c')
    ];
    
    echo json_encode($response);
}

function handleRoot() {
    $response = [
        'message' => 'PHP Server is running!',
        'endpoints' => ['/health', '/time'],
        'description' => 'Simple string operations and greetings',
        'server' => 'PHP',
        'version' => phpversion()
    ];
    
    echo json_encode($response);
}
?>
