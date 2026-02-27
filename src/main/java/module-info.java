open module my.beast.pkg {
    requires beast.pkgmgmt;
    requires beast.base;
    requires org.apache.commons.statistics.distribution;

    exports my.beast.pkg;

    provides beast.base.core.BEASTInterface with
        my.beast.pkg.MyDistribution,
        my.beast.pkg.MyScaleOperator;
}
