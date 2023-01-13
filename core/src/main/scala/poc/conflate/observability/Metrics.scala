package poc.conflate.observability

import io.github.mweirauch.micrometer.jvm.extras.{ProcessMemoryMetrics, ProcessThreadMetrics}
import io.micrometer.core.instrument.binder.jvm.{ClassLoaderMetrics, JvmGcMetrics, JvmMemoryMetrics, JvmThreadMetrics}
import io.micrometer.core.instrument.binder.system.{ProcessorMetrics, UptimeMetrics}
import io.micrometer.prometheus.{PrometheusConfig, PrometheusMeterRegistry}
import io.prometheus.client._
import io.prometheus.client.exporter.HTTPServer
import java.net.InetSocketAddress

object Metrics {

  lazy val micrometer: PrometheusMeterRegistry = {
    val registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    import io.prometheus.client.exporter.common.TextFormat
    registry.scrape(TextFormat.CONTENT_TYPE_OPENMETRICS_100)

    new ClassLoaderMetrics().bindTo(registry)
    new JvmMemoryMetrics().bindTo(registry)
    new JvmGcMetrics().bindTo(registry)
    new ProcessorMetrics().bindTo(registry)
    new JvmThreadMetrics().bindTo(registry)
    new UptimeMetrics().bindTo(registry)
    new ProcessorMetrics().bindTo(registry)
    new ProcessMemoryMetrics().bindTo(registry)
    new ProcessThreadMetrics().bindTo(registry)

    registry
  }

  lazy val registry: CollectorRegistry = micrometer.getPrometheusRegistry

  def httpServer: HTTPServer = new HTTPServer(new InetSocketAddress(1234), registry, true)

}