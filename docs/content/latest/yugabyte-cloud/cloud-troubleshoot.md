---
title: Troubleshoot Yugabyte Cloud
linkTitle: Troubleshoot
description: Troubleshoot issues in Yugabyte Cloud.
headcontent:
image: /images/section_icons/index/quick_start.png
section: YUGABYTE CLOUD
menu:
  latest:
    identifier: cloud-troubleshoot
    weight: 850
isTocNested: true
showAsideToc: true
---

## Connectivity

### Connection timed out

If you are connecting to a cluster and the cluster does not respond, and the connection eventually times out with the following error:

```output
ysqlsh: could not connect to server: Operation timed out
    Is the server running on host "4477b44e-4f4c-4ee4-4f44-f44e4abf4f44.aws.ybdb.io" (44.144.244.144) and accepting
    TCP/IP connections on port 5433?
```

If you are trying to connect to a cluster from your local computer, add your computer to the cluster [IP allow list](../cloud-secure-clusters/add-connections/). If your IP address has changed, add the new IP address.

If you have a VPC configured, add one or more IP addresses from the peered VPC to the cluster [IP allow list](../cloud-secure-clusters/add-connections/).

### Connection closed in cloud shell

If you are connected to a cluster in cloud shell and the message Connection Closed appears.

Cloud shell has a hard limit of 15 minutes for connections. Close the cloud shell window and [launch a new cloud shell session](../cloud-connect/connect-cloud-shell/).

### SSL off

If you are connecting to a cluster using YSQL and see the following error:

```output
ysqlsh: FATAL:  no pg_hba.conf entry for host "144.244.44.44", user "admin", database "yugabyte", SSL off
```

Yugabyte Cloud clusters require an SSL connection. If you set `sslmode` to `disable`, your connection will fail. Refer to [SSL modes in YSQL](../cloud-secure-clusters/cloud-authentication/#ssl-modes-in-ysql).

If you are connecting to a cluster using YCQL and see the following error:

```output
Connection error: ('Unable to connect to any servers', {'44.144.44.4': ConnectionShutdown('Connection to 44.144.44.4 was closed',)})
```

Ensure you are using the `--ssl` option and the path to the cluster CA certificate is correct. YCQL connections require the `--ssl` option and the use of the certificate.

For information on connecting to clusters using a client shell, refer to [Connect via client shells](../cloud-connect/connect-client-shell/).

### Application fails to connect

If the password for the YugabyteDB database account you are using to connect contains special characters (#, %, ^), the driver may fail to parse the URL.

Be sure to encode any special characters in your connection string.

### Password failure connecting to the database

Ensure that you have entered the correct password for the cluster database you are trying to access; refer to the cluster database admin credentials file you downloaded when you created the cluster. The file is named `<cluster name> credentials.txt`.

The database admin credentials are separate from your Yugabyte Cloud credentials, which are used exclusively to log in to the Yugabyte Cloud console.

If you are a database user who was added to the database by an administrator, ask them to either re-send your credentials or reset your database password.

If you are the database admin and are unable to locate your database admin credentials file, contact [Yugabyte Support](https://support.yugabyte.com/hc/en-us/requests/new?ticket_form_id=360003113431).

### VPC networking

If you have set up a VPC network and are unable to connect, verify the following.

#### VPC status is Failed

If you are unable to successfully create the VPC, contact [Yugabyte Support](https://support.yugabyte.com/hc/en-us/requests/new?ticket_form_id=360003113431).

#### Peering connection status is Pending

A peering connection status of _Pending_ indicates that you need to configure your cloud provider to accept the connection. Refer to [Create a peering connection](../cloud-secure-clusters/cloud-vpcs/cloud-add-peering).

#### Peering connection status is Expired (AWS only)

The peering request was not accepted. Recreate the peering connection.

#### Peering connection status is Failed

Select the peering request to display the **Peering Details** sheet and check the **Peered VPC Details** to ensure you entered the correct details for the cloud provider and application VPC.

#### VPC and peering connection are active but your application cannot connect to the cluster

Add the application VPC CIDR address to the [cluster IP allow list](../cloud-secure-clusters/add-connections/). Even with connectivity established between VPCs, the cluster cannot accept connections until the application VPC IP addresses are added to the IP allow list.

## Cluster management

### You are editing your cluster infrastructure and are unable to reduce disk size per node

50GB of disk space per vCPU is included in the base price for standard clusters. If you increased the disk size per node for your cluster, you cannot reduce it. If you need to reduce the disk size for your cluster, contact {{<support-cloud>}}.
