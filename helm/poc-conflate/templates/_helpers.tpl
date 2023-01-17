{{/*
Expand the name of the chart.
*/}}
{{- define "poc-conflate.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "poc-conflate.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "poc-conflate.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "poc-conflate.labels" -}}
helm.sh/chart: {{ include "poc-conflate.chart" . }}
{{ include "poc-conflate.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "poc-conflate.selectorLabels" -}}
app.kubernetes.io/name: {{ include "poc-conflate.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: {{ include "poc-conflate.name" . }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "poc-conflate.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "poc-conflate.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{- define "poc-conflate.kafkaBootstrapServers" -}}
{{- print .Release.Name "-" .Values.kafka.nameOverride "." .Release.Namespace ":" .Values.kafka.service.ports.client | quote }}
{{- end }}