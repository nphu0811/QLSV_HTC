# ============================================
# Stage 1: Build WAR file
# ============================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy source code
COPY src/ src/

# Create output directory structure for WAR
RUN mkdir -p build/webapp/WEB-INF/classes build/webapp/WEB-INF/lib

# Copy web resources
RUN cp -r src/main/webapp/* build/webapp/

# Copy library JARs
RUN cp src/main/webapp/WEB-INF/lib/*.jar build/webapp/WEB-INF/lib/

# Compile Java source files
# Collect all JARs for classpath
RUN CLASSPATH=$(find build/webapp/WEB-INF/lib -name '*.jar' | tr '\n' ':') && \
    CLASSPATH="$CLASSPATH:/opt/java/openjdk/lib/*" && \
    # Download servlet-api for compilation
    mkdir -p /tmp/compile-deps && \
    apt-get update && apt-get install -y --no-install-recommends wget && \
    wget -q -O /tmp/compile-deps/servlet-api.jar \
      "https://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar" && \
    wget -q -O /tmp/compile-deps/jsp-api.jar \
      "https://repo1.maven.org/maven2/javax/servlet/jsp/javax.servlet.jsp-api/2.3.3/javax.servlet.jsp-api-2.3.3.jar" && \
    CLASSPATH="$CLASSPATH:/tmp/compile-deps/servlet-api.jar:/tmp/compile-deps/jsp-api.jar" && \
    find src/main/java -name '*.java' > /tmp/sources.txt && \
    javac -parameters -g -encoding UTF-8 -cp "$CLASSPATH" -d build/webapp/WEB-INF/classes @/tmp/sources.txt

# Build WAR file
RUN cd build/webapp && jar -cf /app/app.war .

# ============================================
# Stage 2: Deploy on Tomcat 8.5
# ============================================
FROM tomcat:8.5-jdk21-temurin

# Remove default webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR file - deploy as ROOT so it's accessible at /
COPY --from=builder /app/app.war /usr/local/tomcat/webapps/ROOT.war

# Also copy as project name context
COPY --from=builder /app/app.war /usr/local/tomcat/webapps/HQTCSDL_De3.war

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
