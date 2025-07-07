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
        $timezone = isset($input['timezone']) ? $input['timezone'] : 'UTC';
    } else {
        $timezone = isset($_GET['timezone']) ? $_GET['timezone'] : 'UTC';
    }
    
    try {
        $tz = new DateTimeZone($timezone);
        $now = new DateTime('now', $tz);
        
        $response = [
            'timezone' => $timezone,
            'current_time' => $now->format('H:i:s'),
            'date' => $now->format('Y-m-d'),
            'day_of_week' => $now->format('l'),
            'unix_timestamp' => $now->getTimestamp(),
            'formatted' => $now->format('F j, Y g:i A T'),
            'server' => 'PHP',
            'timestamp' => $now->format('c')
        ];
        
        echo json_encode($response);
    } catch (Exception $e) {
        http_response_code(400);
        echo json_encode([
            'error' => 'Invalid timezone: ' . $e->getMessage(),
            'server' => 'PHP'
        ]);
    }
}

function handleRoot() {
    $response = [
        'message' => 'PHP Server is running!',
        'endpoints' => ['/health', '/time'],
        'server' => 'PHP',
        'version' => phpversion()
    ];
    
    echo json_encode($response);
}
?>
