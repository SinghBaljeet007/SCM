console.log("contact_modal js");

const baseURL = "http://localhost:8081";

const viewContactModal = document.getElementById('view-contact-modal');

// options with default values
const options = {
    placement: 'bottom-right',
    backdrop: 'dynamic',
    backdropClasses:
        'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
    closable: true,
    onHide: () => {
        console.log('modal is hidden');
    },
    onShow: () => {
        console.log('modal is shown');
    },
    onToggle: () => {
        console.log('modal has been toggled');
    },
};

// instance options object
const instanceOptions = {
    id: 'view-contact-modal',
    override: true
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal() {
    contactModal.show();
}

function closeContactModal() {
    contactModal.hide();
}

async function loadContactData(id) {
    console.log(id);

    // function call to load data
    try {
        const data = await (await fetch(`${baseURL}/api/contacts/${id}`)).json();
        console.log(data);
        document.querySelector("#contact_name").innerHTML = data.name;
        document.querySelector('#contact_email').innerHTML = data.email;
        document.querySelector("#contact_image").src = data.picture;
        document.querySelector("#contact_address").innerHTML = data.address;
        document.querySelector("#contact_mobile").innerHTML = data.mobile;
        document.querySelector("#contact_about").innerHTML = data.description;
        const contactFavorite = document.querySelector("#contact_favorite");
        if (data.favorite) {
            contactFavorite.innerHTML =
                "<i class='fa-solid fa-star text-yellow-400'></i><i class='fa-solid fa-star text-yellow-400'></i><i class='fa-solid fa-star text-yellow-400'></i><i class='fa-solid fa-star text-yellow-400'></i>";
        } else {
            contactFavorite.innerHTML = "<i class='fa-regular fa-star'></i><i class='fa-regular fa-star'></i><i class='fa-regular fa-star'></i><i class='fa-regular fa-star'></i>";
        }

        document.querySelector("#contact_website").href = data.websiteLink;
        document.querySelector("#contact_website").innerHTML = data.websiteLink;
        document.querySelector("#contact_linkedIn").href = data.linkedInLink;
        document.querySelector("#contact_linkedIn").innerHTML = data.linkedInLink;
        openContactModal();
    } catch (error) {
        console.log("Error: ", error);
    }

}

// delete contact via sweet alert popup

async function deleteContact(id) {
    Swal.fire({
        title: "Do you want to delete this contact?",
        icon: "warning",
        showCancelButton: true,
        cancelButtonColor: '#aaa',
        confirmButtonText: "Delete",
        confirmButtonColor: '#d33',
    }).then((result) => {
        /* Read more about isConfirmed, isDenied below */
        if (result.isConfirmed) {
            const url = `${baseURL}/user/contacts/delete/` + id;
            window.location.replace(url);
            // Swal.fire({
            //     title: "Deleted!",
            //     text: "Your contact has been deleted.",
            //     icon: "success",
            //     showConfirmButton: true,
            //     confirmButtonColor: '#8CD4F5'
            // }
            // );
        }
    });
}