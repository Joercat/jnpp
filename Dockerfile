FROM ubuntu:22.04

# Set environment variables to avoid interactive prompts
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC

# Install all required packages
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    gnupg \
    lsb-release \
    ca-certificates \
    supervisor \
    nginx \
    openjdk-17-jdk \
    php8.1 \
    php8.1-fpm \
    python3 \
    python3-pip \
    tzdata \
    && rm -rf /var/lib/apt/lists/*

# Install Node.js
# Using Node.js 18 (setup_18.x) as per your original file.
# Consider updating to a newer LTS version like 20 or 22 if compatible with your app.
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs

# Set the main application directory
WORKDIR /app

# Copy Node.js specific files and install its dependencies
# The 'nodejs/' prefix is correct here because your source structure has a 'nodejs' folder.
# This puts package.json into /app/nodejs/ and installs node_modules into /app/nodejs/node_modules
COPY nodejs/package*.json ./nodejs/
RUN cd nodejs && npm install

# Copy the rest of your application's files (including the actual server.js inside nodejs/)
# This copies everything from your project root (where the Dockerfile is) into /app.
# So, /app/nodejs/server.js, /app/python/, /app/java/, /app/php/, etc.
COPY . .

# Install Python dependencies (assuming 'requirements.txt' or similar is not used)
# This command runs from /app, so it's correct for a top-level python/ directory.
RUN pip3 install flask flask-cors

# Compile Java application
# This command runs from /app, assuming your Java source is in 'java/Server.java'
RUN cd java && javac -cp ".:*" Server.java

# Configure nginx
# This copies your nginx.conf from your project root to the correct Nginx path
COPY nginx.conf /etc/nginx/sites-available/default

# Configure supervisor
# This copies your supervisord.conf from your project root to the correct Supervisor path
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Create log directories for supervisor (important for debugging)
RUN mkdir -p /var/log/supervisor

# Expose port 80 as Nginx listens on it
EXPOSE 80

# Start supervisor as the main process
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
