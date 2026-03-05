pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  environment {
    AWS_REGION          = 'us-east-2'
    PROJECT_NAME        = 'nomadtrack'
    ARTIFACTS_BUCKET    = 'nomadtrack-artifacts-906ea42d'
    BACKEND_INSTANCE_ID = 'i-0f0f9ddf8bd0e6275'
    DEPLOY_SSM_DOCUMENT = 'AWS-RunShellScript'
    APP_DIR = '/opt/nomadtrack'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
        steps {
            sh '''
            export JAVA_HOME=/usr/lib/jvm/java-25-amazon-corretto
            export PATH=$JAVA_HOME/bin:$PATH
            java -version
            mvn -B clean compile
            '''
        }
    }


stage('Test') {
    steps {
        sh '''
        export JAVA_HOME=/usr/lib/jvm/java-25-amazon-corretto
        export PATH=$JAVA_HOME/bin:$PATH
        mvn -B test
        '''
    }
}

stage('Package') {
    steps {
        sh '''
        export JAVA_HOME=/usr/lib/jvm/java-25-amazon-corretto
        export PATH=$JAVA_HOME/bin:$PATH
        mvn -B -DskipTests package
        '''
    }
}

    stage('Upload Artifact to S3') {
      steps {
        script {
          def jar = sh(script: "ls -1 target/*.jar | head -n 1", returnStdout: true).trim()
          if (!jar) {
            error("No JAR found in target/. Did the build produce a jar?")
          }

          def key = "${env.PROJECT_NAME}/backend/${env.BUILD_NUMBER}/app.jar"

          sh """
            aws --version
            aws s3 cp "${jar}" "s3://${env.ARTIFACTS_BUCKET}/${key}" --region "${env.AWS_REGION}"
          """

          env.ARTIFACT_KEY = key
        }
      }
    }

    stage('Deploy Backend (SSM to EC2)') {
      steps {
        script {
          def commands = """
            set -e

            echo "Install AWS CLI if missing..."
            sudo dnf -y install awscli || true

            APP_DIR=/opt/nomadtrack
            sudo mkdir -p $APP_DIR
            sudo chown ec2-user:ec2-user $APP_DIR

            echo "Download artifact from S3..."
            aws s3 cp "s3://${ARTIFACTS_BUCKET}/${ARTIFACT_KEY}" "$APP_DIR/app.jar" --region "${AWS_REGION}"

            echo "Create/Update systemd service..."
            sudo tee /etc/systemd/system/nomadtrack.service > /dev/null <<'EOF'
[Unit]
Description=NomadTrack Spring Boot API
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/opt/nomadtrack
EnvironmentFile=/opt/nomadtrack/.env
Environment=SPRING_PROFILES_ACTIVE=prod
ExecStart=/usr/bin/java -jar /opt/nomadtrack/app.jar
Restart=always
RestartSec=5
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

            echo "Restart service..."
            sudo systemctl daemon-reload
            sudo systemctl enable nomadtrack || true
            sudo systemctl restart nomadtrack
            sudo systemctl status nomadtrack --no-pager || true
          """.stripIndent()

          def jsonCommands = commands
            .split("\\n")
            .collect { it.trim() }
            .findAll { it.length() > 0 }
            .collect { it.replace('"','\\"') }

          sh """
            aws ssm send-command \
              --region "${env.AWS_REGION}" \
              --document-name "${env.DEPLOY_SSM_DOCUMENT}" \
              --instance-ids "${env.BACKEND_INSTANCE_ID}" \
              --comment "Deploy NomadTrack backend build ${env.BUILD_NUMBER}" \
              --parameters commands='["${jsonCommands.join('","')}"]'
          """
        }
      }
    }
  }

  post {
    success { echo "Backend deploy triggered. API: https://api.nomadtrack.net" }
    failure { echo "Backend pipeline failed. Check Console Output." }
  }
}