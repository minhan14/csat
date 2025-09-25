# --- Stage 1: The "Builder" Stage ---
# We use a Node.js image to build our React application.
# 'alpine' is a lightweight version of Linux.
FROM node:20-alpine as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the package.json and package-lock.json files first
# This allows Docker to cache our dependencies for faster builds
COPY package*.json ./

# Install all the project dependencies
RUN npm install

# Copy the rest of our application source code
COPY . .

# Run the production build command
# This creates an optimized 'dist' folder with our static files
RUN npm run build

# --- Stage 2: The "Server" Stage ---
# We now switch to a very lightweight Nginx web server image
FROM nginx:stable-alpine

# Copy our custom Nginx configuration file
# This is crucial for making React Router work correctly
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the built static files from the "builder" stage
# into the public directory of the Nginx server
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port 80, which is the default port for Nginx
EXPOSE 80

# The default Nginx command will start the server automatically
CMD ["nginx", "-g", "daemon off;"]
