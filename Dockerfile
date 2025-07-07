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
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs

# Create app directory
WORKDIR /app

# Copy all files
COPY . .

# Install Node.js dependencies
RUN cd nodejs && npm install

# Install Python dependencies
RUN pip3 install flask flask-cors

# Compile Java application
RUN cd java && javac -cp ".:*" Server.java

# Configure nginx
COPY nginx.conf /etc/nginx/sites-available/default

# Configure supervisor
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Expose port
EXPOSE 80

# Start supervisor
CMD ["/usr/bin/supervisord"]
